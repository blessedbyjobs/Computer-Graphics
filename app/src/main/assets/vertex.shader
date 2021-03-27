uniform mat4 u_modelViewProjectionMatrix;
attribute vec3 a_vertex;
attribute vec3 a_normal;
attribute vec4 a_color;
varying vec3 v_vertex;
varying vec3 v_normal;
varying vec4 v_color;
void main() {
v_vertex=a_vertex;
vec3 n_normal=normalize(a_normal);
v_normal=n_normal;
v_color=a_color;
gl_Position = u_modelViewProjectionMatrix * vec4(a_vertex,1.0);
}