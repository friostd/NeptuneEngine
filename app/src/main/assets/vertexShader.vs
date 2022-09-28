uniform mat4 projectionMatrix;
attribute vec4 vPosition;
uniform mat4 model;

void main() {
  gl_Position = projectionMatrix * model * vPosition;
}