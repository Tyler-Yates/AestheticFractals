#include <iostream>

#include "common.h"
#include "expression.h"
#include "fractal.h"

using namespace std;

int window_width=600, window_height=600;
float window_aspect = window_width / static_cast<float>(window_height);
float zoom = 1;
int windowID;

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
  int screen_width = glutGet(GLUT_SCREEN_WIDTH);
  int screen_height = glutGet(GLUT_SCREEN_HEIGHT);

  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);  // clear the screen buffer

  int size = fractals.size();
  if (size == 1) {
    //    glViewport(0, 0, window_width, window_height);
    adjustBounds(fractals[0]);
    fractals[0].paint();
  } else {
    //    for (int i = 0; i <= size / 2; i++) {
      //      for (int j = 0; j < (size+1) / 2; j++) {
        //        glViewport(j*window_width/((size+1)/2),i*window_height/(size), window_width/((size+1)/2), window_height/(size));
    for (int i = 0; i < size; i++) {
      adjustBounds(fractals[i]);
      fractals[i].paint();
    }
  }
  
  glFlush();
  glutSwapBuffers(); 
}

//****************************************
void Reshape(int w, int h){ // function called when window size is changed
  glViewport (0, 0, (GLsizei)w, (GLsizei)h); // set new dimension of viewable screen
  glutPostRedisplay();
}

//****************************************
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
    // Rotation                                                                                                     
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
  case 32: // Spacebar
    fractals[0].mutateConstants();
    zoom = 1;
    glutPostRedisplay();
    break;
  case 'F': 
  case 'f':
    if(fullScreen){
      glutReshapeWindow(window_width,window_height); // sets default window size
      GLsizei windowX = (glutGet(GLUT_SCREEN_WIDTH)-window_width)/2;
      GLsizei windowY = (glutGet(GLUT_SCREEN_HEIGHT)-window_height)/2;
      glutPositionWindow(windowX, windowY); // centers window on the screen
      fullScreen = false;
    }
    else{
      fullScreen = true;
      glutFullScreen(); 
    }
    glutPostRedisplay();
    break;
  case 27 : // escape key - close the program
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

//****************************************
int main(int argc, char** argv){
  glutInit(&argc, argv);
  window_width = glutGet(GLUT_SCREEN_WIDTH);
  window_height = glutGet(GLUT_SCREEN_HEIGHT);
  window_aspect = window_width / static_cast<float>(window_height);

  createPalette();
  glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGBA);

  GLsizei windowX = (glutGet(GLUT_SCREEN_WIDTH)-window_width)/2;
  GLsizei windowY = (glutGet(GLUT_SCREEN_HEIGHT)-window_height)/2;
  glutInitWindowPosition(windowX, windowY);
  glutInitWindowSize(window_width, window_height);
  windowID = glutCreateWindow("Aesthetic Fractals");
  glutFullScreen();
  fullScreen=true;

  if (argc < 3) {
    fractals.push_back(CliffordAttractor("sin( a * y ) + c * cos(a * x)", "sin(b * x) + d * cos(b * y)"));
  } else {
    for (int i = 1; i < argc - 1; i+=2)
      fractals.push_back(CliffordAttractor(argv[i], argv[i+1]));
  } 

  glClearColor(0, 0, 0, 0);

  // Enable Blending for transparency
  glShadeModel(GL_SMOOTH);
  glEnable(GL_BLEND);
  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  //glEnable(GL_DEPTH_TEST);
  
  // set the event handling methods
  glutDisplayFunc(Repaint);
  glutReshapeFunc(Reshape);
  glutMouseFunc(MouseButton);
  glutMotionFunc(MouseMotion);
  glutKeyboardFunc(Keyboard);
  glutMainLoop();

  return 0;
}

void adjustBounds(AttractorFractal f) {
  glMatrixMode (GL_PROJECTION);
  glLoadIdentity();
  gluPerspective(40.0, window_width/window_height, 1, 1500);

  glMatrixMode(GL_MODELVIEW);
  glLoadIdentity();
  BoundingBox bbox = f.getbb();
  float maxDist = (bbox.max-bbox.min).max();
  Vec3f eye = Vec3f::makeVec(0.0f*maxDist, 0.0f*maxDist, 1.5f*maxDist);
  gluLookAt(eye[0]*zoom, eye[1]*zoom, eye[2]*zoom,
            0, 0, 0,
            0, 1, 0);

  glMultMatrixf(rot_matrix);

  // Move the origin up                                                                                             
  // glTranslatef(0, -maxDist/8, 0);
}
