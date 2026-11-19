/* Open source replacement for com.google.android.chromeos.lowlatencystylus.gpu.InkFrameBufferRenderer
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.gpu;

import android.opengl.GLES20;
import android.opengl.GLException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Full-screen-quad framebuffer renderer used by {@link GLInkRenderer} to
 * implement single-buffered mode drawing.
 */
class InkFrameBufferRenderer {
    private static final String TAG = "InkFrameBufferRenderer";
    private static final String VERTEX_SHADER =
            "attribute vec4 a_position;\n"
            + "varying vec2 v_texcoord;\n"
            + "void main() {\n"
            + "  gl_Position = vec4(a_position.xy, 0.0, 1.0);\n"
            + "  v_texcoord = (a_position.xy / 2.0) + 0.5;\n"
            + "}\n";
    private static final String FRAG_SHADER =
            "precision mediump float;\n"
            + "varying vec2 v_texcoord;\n"
            + "uniform sampler2D u_texture;\n"
            + "void main() {\n"
            + "  gl_FragColor = texture2D(u_texture, v_texcoord);\n"
            + "}\n";
    private static final int VERTEX_POSITION_DIM = 2;
    private static final int VERTEX_STRIPE_SIZE = 8;
    private static final int VERTICES_COUNT = 6;
    private static final int VERTICES_SIZE = 48;

    private static final float[] VERTICES_FULL_SCREEN = {
            -1.0f, -1.0f,
            -1.0f,  1.0f,
             1.0f,  1.0f,
            -1.0f, -1.0f,
             1.0f,  1.0f,
             1.0f, -1.0f };

    private static final FloatBuffer VERTEX_BUFFER = getVertexBuffer();

    private final int mFrameBuffer;
    private final int mHeight;
    private final int mWidth;
    private final GLShader mShader;
    private final int mTexture;
    private final int mTextureHandle;
    private final int mVertexAttribHandle;
    private final int mVertexBuffer;

    private static FloatBuffer getVertexBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(VERTICES_SIZE);
        buffer.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = buffer.asFloatBuffer();
        vertexBuffer.put(VERTICES_FULL_SCREEN);
        vertexBuffer.position(0);
        return vertexBuffer;
    }

    InkFrameBufferRenderer(int width, int height) throws GLException {
        this.mShader = new GLShader(VERTEX_SHADER, FRAG_SHADER);
        this.mShader.setLogTag(TAG);
        this.mHeight = height;
        this.mWidth = width;
        this.mVertexAttribHandle =
                GLES20.glGetAttribLocation(this.mShader.getProgram(), "a_position");
        this.mTextureHandle =
                GLES20.glGetUniformLocation(this.mShader.getProgram(), "u_texture");
        this.mShader.checkGlError("GL Get Locations");
        int[] tmp = new int[1];
        GLES20.glGenBuffers(1, tmp, 0);
        this.mVertexBuffer = tmp[0];
        GLES20.glGenFramebuffers(1, tmp, 0);
        this.mFrameBuffer = tmp[0];
        GLES20.glGenTextures(1, tmp, 0);
        this.mTexture = tmp[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, this.mFrameBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.mTexture);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, this.mTexture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    void useFrameBuffer(boolean choice) {
        if (choice) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, this.mFrameBuffer);
            GLES20.glViewport(0, 0, this.mWidth, this.mHeight);
        } else {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            GLES20.glViewport(0, 0, this.mWidth, this.mHeight);
        }
    }

    void draw() {
        VERTEX_BUFFER.position(0);
        GLES20.glUseProgram(this.mShader.getProgram());
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.mTexture);
        GLES20.glUniform1i(this.mTextureHandle, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.mVertexBuffer);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, VERTICES_SIZE, VERTEX_BUFFER,
                GLES20.GL_STATIC_DRAW);
        GLES20.glVertexAttribPointer(this.mVertexAttribHandle, VERTEX_POSITION_DIM,
                GLES20.GL_FLOAT, false, VERTEX_STRIPE_SIZE, 0);
        GLES20.glEnableVertexAttribArray(this.mVertexAttribHandle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTICES_COUNT);
        GLES20.glDisableVertexAttribArray(this.mVertexAttribHandle);
    }
}
