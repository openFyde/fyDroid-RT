/* Open source replacement for com.google.android.chromeos.lowlatencystylus.gpu.GLShader
 * from the proprietary com.google.android.chromeos jar. */
package com.google.android.chromeos.lowlatencystylus.gpu;

import android.opengl.GLES20;
import android.opengl.GLException;
import android.text.TextUtils;
import android.util.Log;

/**
 * Thin wrapper around a GLES2 shader program.
 */
class GLShader {
    private String mLogTag = "GLShader";
    private final int mProgram;

    GLShader(String vertexShaderCode, String fragmentShaderCode) throws RuntimeException {
        if (TextUtils.isEmpty(vertexShaderCode)) {
            throw new NullPointerException("vertexShaderCode cannot be null");
        }
        if (TextUtils.isEmpty(fragmentShaderCode)) {
            throw new NullPointerException("fragmentShaderCode cannot be null");
        }
        this.mProgram = GLES20.glCreateProgram();
        if (this.mProgram == 0) {
            throw new RuntimeException("Error creating a new shader program.");
        }
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        GLES20.glAttachShader(this.mProgram, vertexShader);
        checkGlError("Error attaching the vertex shader");
        GLES20.glAttachShader(this.mProgram, fragmentShader);
        checkGlError("Error attaching the fragment shader");
        GLES20.glLinkProgram(this.mProgram);
        checkGlError("Error linking the shader program");
    }

    void setLogTag(String logTag) {
        this.mLogTag = TextUtils.isEmpty(logTag) ? this.mLogTag : logTag;
    }

    int getProgram() {
        return this.mProgram;
    }

    private int loadShader(int shaderType, String shaderCode) throws IllegalArgumentException {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            checkGlError("Error loading shader: " + shaderType);
        }
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            String infoLog = GLES20.glGetShaderInfoLog(shader);
            Log.e(this.mLogTag, infoLog);
            GLES20.glDeleteShader(shader);
            throw new IllegalArgumentException("Shader compilation failed: " + infoLog);
        }
        return shader;
    }

    void checkGlError(String msg) throws GLException {
        int error = GLES20.glGetError();
        if (error != 0) {
            GLException exception = new GLException(error, msg);
            Log.e(this.mLogTag, "GlContext error:", exception);
            throw exception;
        }
    }
}
