#include <iostream>

#include "common.h"
#include "expression.h"
#include "fractal.h"

using namespace std;

vector<AttractorFractal> fractals;

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

//****************************************
void Repaint() {
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);  // clear the screen buffer
  for (int i = 0; i < fractals.size(); i++) {
    fractals[i].paint();
  }
  glutSwapBuffers(); 
}

//****************************************
void Reshape(int w, int h){ // function called when window size is changed
  //  stepX = (maxX-minX)/(GLfloat)w; // calculate new value of step along X axis
  //  stepY = (maxY-minY)/(GLfloat)h; // calculate new value of step along Y axis
  glViewport (0, 0, (GLsizei)w, (GLsizei)h); // set new dimension of viewable screen
  glutPostRedisplay(); // repaint the window
}

//****************************************
void Keyboard(unsigned char key, int x, int y){ 
  switch(key){
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
  createPalette();
  glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGBA);
  glClearColor(0, 0, 0, 0);

  fractals.push_back(CliffordAttractor("sin( a * y ) + c * cos(a * x)", "sin(b * x) + d * cos(b * y)"));
  //fractals.push_back(AttractorFractal("sin( -1.4 * y ) + cos( -1.4 * x )", "sin( 1.6 * x ) + 0.7 * cos( 1.6 * y )"));
  //  fractals.push_back(AttractorFractal("(1 * x) - (0.1 * y)", "(0.1 * x) + (0.99 * y)"));

  Vec4d bounds = fractals[0].getBounds();

  //Center window
  double fractal_width = bounds[1] - bounds[0];
  double fractal_height = bounds[3] - bounds[2];
  double fractal_ratio = fractal_width/fractal_height;
  
  int screen_width = glutGet(GLUT_SCREEN_WIDTH);
  int screen_height = glutGet(GLUT_SCREEN_HEIGHT);
  double screen_ratio = screen_width/screen_height;

  if (fractal_ratio >= screen_ratio) {
    window_width = glutGet(GLUT_SCREEN_WIDTH);
    window_height = window_width/fractal_ratio;
  } else {
    window_height = glutGet(GLUT_SCREEN_HEIGHT);
    window_width = fractal_ratio*window_height;
  }

  GLsizei windowX = (glutGet(GLUT_SCREEN_WIDTH)-window_width)/2;
  GLsizei windowY = (glutGet(GLUT_SCREEN_HEIGHT)-window_height)/2;
  glutInitWindowPosition(windowX, windowY);
  glutInitWindowSize(window_width, window_height);
  windowID = glutCreateWindow("Aesthetic Fractals");

  glShadeModel(GL_SMOOTH);
  glEnable(GL_BLEND);
  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  //  glEnable(GL_DEPTH_TEST);
  glViewport (0, 0, (GLsizei) window_width, (GLsizei) window_height);
  glMatrixMode (GL_PROJECTION);
  glLoadIdentity();

  glOrtho(bounds[0], bounds[1], bounds[2], bounds[3], ((GLfloat)-1), (GLfloat)1);

  // set the event handling methods
  glutDisplayFunc(Repaint);
  glutReshapeFunc(Reshape);
  glutKeyboardFunc(Keyboard);
  
  glutMainLoop();

  return 0;
}
