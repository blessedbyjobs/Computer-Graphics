precision mediump float;
uniform vec3 u_camera;
uniform vec3 u_lightPosition;
varying vec3 v_vertex;
varying vec3 v_normal;
varying vec4 v_color;
void main() {
vec3 n_normal=normalize(v_normal);
vec3 lightvector = normalize(u_lightPosition - v_vertex);
vec3 lookvector = normalize(u_camera - v_vertex);
float ambient=0.4;
float k_diffuse=0.6;
float k_specular=0.5;
float diffuse = k_diffuse * max(dot(n_normal, lightvector), 0.0);
vec3 reflectvector = reflect(-lightvector, n_normal);
float specular = k_specular * pow( max(dot(lookvector,reflectvector),0.0), 40.0 );
vec4 one=vec4(1.0,1.0,1.0,1.0);
vec4 lightColor = (ambient+diffuse+specular)*one;
gl_FragColor = mix(lightColor, v_color, 0.5);
}