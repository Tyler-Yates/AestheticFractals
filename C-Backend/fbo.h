#ifndef __FBO_H__
#define __FBO_H__
#include <string>
#include "common.h"

using namespace std;

static GLuint image_width, image_height;

class ExternalRenderer {
public:
  static void switchToExternalTarget();
  static void switchToWindowTarget();
  static void getNewRenderBuffer(GLuint *buffer);
  static void   deleteRenderBuffer(GLuint *renderbuffer);
  static void outputToImage(string name);
};
#endif
