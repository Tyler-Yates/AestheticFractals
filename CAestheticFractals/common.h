#ifndef __COMMON_H__
#define __COMMON_H__

#include <stdlib.h>
#ifdef _WIN32
#include <windows.h>
#endif
#ifdef __MAC__
#include <OpenGL/gl.h>
#include <GLUT/glut.h>
#else
#include <GL/gl.h>
#include <GL/glut.h>
#endif

#pragma hdrstop

int window_width=600;
int window_height=600; 
int windowID;

GLfloat minX = -2.2f, maxX = 0.8f, minY = -1.5f, maxY = 1.5; // complex plane boundaries                  
//GLfloat stepX = (maxX - minX)/(GLfloat)window_width;
//GLfloat stepY = (maxY - minY)/(GLfloat)window_height;
  
const int paletteSize = 128;
GLfloat palette[paletteSize][3];

const GLfloat radius = 5.0f;
bool fullScreen=false;

struct Color {
  Color() {}
  Color(GLfloat _r, GLfloat _g, GLfloat _b, GLfloat _a=1.0f) {
    c[0] = _r;
    c[1] = _g;
    c[2] = _b;
    c[3] = _a;    
  }
  operator GLfloat*() { return c; }
  GLfloat c[4];
};


Color kRed = Color(1, 0, 0);
Color kGreen = Color(0, 1, 0);
Color kBlue = Color(0, 0, 1);
Color kYellow = Color(1, 1, 0);
Color kViolet = Color(0.541176, 0.168627, 0.886275);
Color kBrown = Color(0.647, 0.1647, 0.1647);
Color kOrange = Color(1, 0.498039, 0.313725);
Color kBlack = Color(0, 0, 0);
Color kWhite = Color(1, 1, 1);

#endif
