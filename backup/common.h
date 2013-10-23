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
#include <GL/glew.h>
#include <GL/gl.h>
#include <GL/glut.h>
#endif

#pragma hdrstop

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

#endif
