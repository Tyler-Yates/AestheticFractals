#include <cstring>
#include <iostream>
#include <sstream>
#include <iomanip>

#include "common.h"
#include "expression.h"
#include "fractal.h"
#include "fbo.h"

using namespace std;

// Legacy variables for Mandelbrot Set
GLfloat minX = -2.2f, maxX = 0.8f, minY = -1.5f, maxY = 1.5; // complex plane boundaries
const GLfloat radius = 5.0f;
const int paletteSize = 128;
GLfloat palette[paletteSize][3];

// Window Variables
int windowID;
int window_width=600, window_height=600;
float window_aspect = window_width / static_cast<float>(window_height);

bool fullScreen=false;

// GLUT Utility Variables
float zoom = 1;
float mouse_x, mouse_y;
float arcmouse_x, arcmouse_y, arcmouse_z;

bool right_mouse_button= false;
bool left_mouse_button = false;

GLfloat rot_matrix[16] = {1, 0, 0, 0,
                          0, 1, 0, 0,
                          0, 0, 1, 0,
                          0, 0, 0, 1};


vector<CliffordAttractor> fractals;
//vector<AttractorFractal> fractals;

//****************************************
/*
  Legacy palette generator for Mandelbrot Set
 */
GLfloat* calculateColor(GLfloat u, GLfloat v){
  GLfloat re = u;
  GLfloat im = v;
  GLfloat tempRe=0.0;
  for(int i=0; i < paletteSize; i++){
    tempRe = re*re - im*im + u;
    im = re * im * 2 + v;
    re = tempRe;
    if( (re*re + im*im) > radius ){
      return palette[i];
    }
  }
  return kBlack;
}

void adjustBounds(AttractorFractal f);


//****************************************
void Repaint() {
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

  int size = fractals.size();
  for (int i = 0; i < size; i++) {
    adjustBounds(fractals[i]);
    fractals[i].paint();
  }

  glFlush();
  glutSwapBuffers();
}

//****************************************
/*
  Update viewport in response to change in window size
 */
void Reshape(int w, int h){
  glViewport (0, 0, (GLsizei)w, (GLsizei)h);
  glutPostRedisplay();
}

void resize() {
  glutReshapeWindow(window_width,window_height); // Default window size
  GLsizei windowX = (glutGet(GLUT_SCREEN_WIDTH)-window_width)/2;
  GLsizei windowY = (glutGet(GLUT_SCREEN_HEIGHT)-window_height)/2;
  glutPositionWindow(windowX, windowY); // Center window
}

void resize(int w, int h) {
  window_width = w;
  window_height = h;
  ExternalRenderer::setImageWidth(window_width);
  ExternalRenderer::setImageHeight(window_height);
  resize();
  Reshape(nw, h);
}

//****************************************
/*
  Return a vector from fractal center to world coordinates of mouse
 */
Vec3f arcSnap(float x, float y) {
  x = (2.0*x / window_width) - 1;
  y = (2.0*y / window_height) - 1;

  float mag2 = x * x + y * y;
  float mag = sqrt(mag2);

  if (mag > 1) {
    x = x*0.999 / mag;  // mult by .999 to account for edge cases of rounding up
    y = y*0.999 / mag;
  }

  float z = sqrt(1.0 - (x*x + y*y));
  return Vec3f::makeVec(x, y, z);
}

//****************************************
void MouseButton(int button, int state, int x, int y) {
  y = window_height - y;

  if (button == GLUT_LEFT_BUTTON) {
    Vec3f arc_coords = arcSnap(x, y);
    arcmouse_x = arc_coords[0];
    arcmouse_y = arc_coords[1];
    arcmouse_z = arc_coords[2];

    left_mouse_button = !state;  // state==0 if down
  }
  if (button == GLUT_RIGHT_BUTTON) {
    right_mouse_button = !state;  // state==0 if down
  }

  mouse_x = x, mouse_y = y;
  glutPostRedisplay();
}

//****************************************
void MouseMotion(int x, int y) {
  y = window_height - y;

  if (left_mouse_button) {
    // Arc-ball rotational movement
    Vec3f arc_coords = arcSnap(x, y);
    float fx = arc_coords[0];
    float fy = arc_coords[1];
    float fz = arc_coords[2];

    // Find rotational axis
    float normal_x = arcmouse_y*fz - arcmouse_z*fy;
    float normal_y = arcmouse_z*fx - arcmouse_x*fz;
    float normal_z = arcmouse_x*fy - arcmouse_y*fx;

    // Find rotational angle
    float ax = sqrt(normal_x*normal_x +
                    normal_y*normal_y +
                    normal_z*normal_z);

    float ay = arcmouse_x*fx + arcmouse_y*fy + arcmouse_z*fz;
    float angle = atan2(ax, ay)*180/3.14159;

    // Modify and save rotation matrix
    glLoadIdentity();
    glRotatef(angle, normal_x, normal_y, normal_z);
    glMultMatrixf(rot_matrix);
    glGetFloatv(GL_MODELVIEW_MATRIX, rot_matrix);

    arcmouse_x = fx, arcmouse_y = fy, arcmouse_z = fz;

  } else if (right_mouse_button && y && mouse_y) {
    // Zoom: Multiplies current zoom by ratio between initial and current y
    float smy = mouse_y+window_height;
    float sy = y+window_height;
    float dy;

    if (sy < 0 && smy < 0) {
      dy = abs(smy/sy);
    } else {
      dy = abs(sy/smy);
    }

    zoom *= dy;
  }

  mouse_x = x, mouse_y = y;
  glutPostRedisplay();
}


void Keyboard(unsigned char key, int x, int y){
  switch(key){
  case 32:  // (Spacebar) Mutate fractal
    fractals[0].mutateConstants();
    zoom = 1;
    glutPostRedisplay();
    break;
  case 'F':  // Toggle fullscreen
  case 'f':
    if(fullScreen){
      resize();
      fullScreen = false;
    }
    else{
      fullScreen = true;
      glutFullScreen();
    }
    glutPostRedisplay();
    break;
  case 's':  // Save to test.png
    ExternalRenderer::outputToImage("test");
    break;
  case 27 : // (ESC) close the program
    glutDestroyWindow(windowID);
    exit(0);
    break;
  }
}

//****************************************
void createPalette(){
  for(int i=0; i < 32; i++){
    palette[i][0] = (8*i)/(GLfloat)255;
    palette[i][1] = (128-4*i)/(GLfloat)255;
    palette[i][2] = (255-8*i)/(GLfloat)255;
  }
  for(int i=0; i < 32; i++){
    palette[32+i][0] = (GLfloat)1;
    palette[32+i][1] = (8*i)/(GLfloat)255;
    palette[32+i][2] = (GLfloat)0;
  }
  for(int i=0; i < 32; i++){
    palette[64+i][0] = (128-4*i)/(GLfloat)255;
    palette[64+i][1] = (GLfloat)1;
    palette[64+i][2] = (8*i)/(GLfloat)255;
  }
  for(int i=0; i < 32; i++){
    palette[96+i][0] = (GLfloat)0;
    palette[96+i][1] = (255-8*i)/(GLfloat)255;
    palette[96+i][2] = (8*i)/(GLfloat)255;
  }
}

void renderInGlut() {
  glutFullScreen();
  fullScreen=true;

  // set the event handling methods
  glutDisplayFunc(Repaint);
  glutReshapeFunc(Reshape);
  glutMouseFunc(MouseButton);
  glutMotionFunc(MouseMotion);
  glutKeyboardFunc(Keyboard);
  glutMainLoop();
}

void error_callback(int error, const char* description)
{
  fputs(description, stderr);
}


//****************************************
int main(int argc, char** argv){
  glutInit(&argc, argv);
  window_width = glutGet(GLUT_SCREEN_WIDTH);
  window_height = glutGet(GLUT_SCREEN_HEIGHT);
  window_aspect = window_width / static_cast<float>(window_height);

  //createPalette();
  glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGBA);

  GLsizei windowX = (glutGet(GLUT_SCREEN_WIDTH)-window_width)/2;
  GLsizei windowY = (glutGet(GLUT_SCREEN_HEIGHT)-window_height)/2;
  glutInitWindowPosition(windowX, windowY);
  glutInitWindowSize(window_width, window_height);

  windowID = glutCreateWindow("Aesthetic Fractals");
  glClearColor(0,0,0,1);

  // Enable Blending for transparency
  glShadeModel(GL_SMOOTH);
  glEnable(GL_BLEND);
  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  //glEnable(GL_DEPTH_TEST);

  // Enable GLEW library for External rendering
  GLenum err = glewInit();
  if (GLEW_OK != err) {
    fprintf(stderr, "Error: %s\n", glewGetErrorString(err));
    exit(0);
  }

  ExternalRenderer::setImageWidth(window_width);
  ExternalRenderer::setImageHeight(window_height);

  if (argc >= 3) {
    bool savingImage = (strcmp(argv[1],"-save") == 0);
    int startIndex = (savingImage ? 2 : 1);
    GLuint renderbuffer;
    char* imgName;

    if (savingImage) {
      /*
        User wants to save the fractal image.
        ./aesthetics -save [ -p PRECISION_POINTS ] [ -s WIDTH HEIGHT ] IMG_NAME
        EXPR_X EXPR_Y EXPR_Z EXPR_R EXPR_G EXPR_B
      */

      // Create new offscreen renderbuffer
      glutHideWindow();
      ExternalRenderer::switchToExternalTarget();
      ExternalRenderer::getNewRenderBuffer(&renderbuffer);
      glutHideWindow();
    }

    // Iterate over all fractal definitions provided by user
    for (int i = startIndex; i <= argc - 6; i+=6) {

      // Set number of points to calculate (default 1,000,000)
      if (strcmp(argv[i],"-p") == 0) {
        setPrecisionPoints(stoi(argv[++i]));
        ++i;
      }

      // Resize window
      if (strcmp(argv[i],"-s") == 0) {
        int width = stoi(argv[++i]);
        int height = stoi(argv[++i]);
        resize(width, height);
        ++i;
      }

      if (savingImage) {
        imgName = argv[i++];
      }

      // Calculate points
      CliffordAttractor ca(argv[i], argv[i+1], argv[i+2], argv[i+3], argv[i+4], argv[i+5]);
      fractals.push_back(ca);

      if (savingImage) {
        Repaint();
        glutHideWindow();

        // Save Image
        ExternalRenderer::outputToImage(imgName);
        ca.saveToFile(imgName);
        fractals.clear();
      } else {
        renderInGlut();
      }
    }

    if (savingImage)
      ExternalRenderer::deleteRenderBuffer(&renderbuffer);

  } else {
    // No fractal definitions provided; draw an example fractal.
    fractals.push_back(CliffordAttractor("sin(-1.4 * y) + cos(-1.4 * x)", "sin(1.6 * x) + 0.7 * cos(1.6 * y)", "x", "x", "y", "z"));
    //fractals.push_back(CliffordAttractor("sin( a * y ) + c * cos(a * x)", "sin(b * x) + d * cos(b * y)"));
    renderInGlut();
  }

  return 0;
}

/*
  Adjust the camera to fit Fractal f.
 */
void adjustBounds(AttractorFractal f) {
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
  gluPerspective(40.0, window_width/window_height, 1, 1500);

  glMatrixMode(GL_MODELVIEW);
  glLoadIdentity();

  BoundingBox bbox = f.getbb();
  float x, y, z, r, fdistance;
  x = (bbox.max[0] + bbox.min[0]) / 2.0f;
  y = (bbox.max[1] + bbox.min[1]) / 2.0f;
  z = (bbox.max[2] + bbox.min[2]) / 2.0f;
  r = sqrt((bbox.max[0] - x)*(bbox.max[0] - x) + (bbox.max[1] - y)*(bbox.max[1] - y) + (bbox.max[2] - z)*(bbox.max[2] - z));
  fdistance = r / .3697f;
  gluLookAt(0.0f, 0.0f, fdistance*zoom, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
  glTranslatef(-x, -y, -z);

  glMultMatrixf(rot_matrix);
}

/*
  TODO: Use glfw instead of glut if detected, for hidden window support.
  http://stackoverflow.com/questions/17768008/how-to-build-install-glfw-3-and-use-it-in-a-linux-project
  int glfwStartup() {
  // Initialise GLFW
  if( !glfwInit() )
  {
  fprintf( stderr, "Failed to initialize GLFW\n" );
  return -1;
  }

  glfwSetErrorCallback(error_callback);
  glfwWindowHint(GLFW_SAMPLES, 4); // 4x antialiasing
  glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // We want OpenGL 3.3
  glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
  //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); //We don't want the old OpenGL
  glfwWindowHint(GLFW_VISIBLE, 0);

  // Open a window and create its OpenGL context
  GLFWwindow* window = glfwCreateWindow( 1024, 768, "Aesthetic Fractals glfw Test", NULL, NULL );
  if (!window)
  {
  fprintf( stderr, "Failed to open GLFW window\n" );
  glfwTerminate();
  return -1;
  }

  // Enable GLEW library for External rendering
  GLenum err = glewInit();
  if (GLEW_OK != err) {
  fprintf(stderr, "Error: %s\n", glewGetErrorString(err));
  exit(0);
  }

  glfwMakeContextCurrent(window);
  return 1;
  }
*/
