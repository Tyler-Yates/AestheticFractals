#include <iostream>

#include "fbo.h"
#include "libs/CImg-1.5.6/CImg.h"

using namespace cimg_library;

GLuint framebuffer;
GLuint status;
GLuint image_width = 1024, image_height = 1024;

void ExternalRenderer::switchToExternalTarget() {
  cout << "test" << endl;
  glGenFramebuffers(1, &framebuffer);
  cout << "test" << endl;
  glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
}

void ExternalRenderer::switchToWindowTarget() {
  glBindFramebuffer(GL_FRAMEBUFFER, 0);
}

void ExternalRenderer::getNewRenderBuffer(GLuint *buffer) {
  glGenRenderbuffers(1, buffer);
  glBindRenderbuffer(GL_RENDERBUFFER, *buffer);
  glRenderbufferStorage(GL_RENDERBUFFER, GL_RGBA8, image_width, image_height);
  glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, *buffer);

  status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
  if (status != GL_FRAMEBUFFER_COMPLETE) {
    // Handle errors
  }
}

void ExternalRenderer::deleteRenderBuffer(GLuint *renderbuffer) {
  glDeleteRenderbuffers(1, renderbuffer);
}

void ExternalRenderer::outputToImage(string name) {
  /*  glPixelStorei(GL_PACK_ROW_LENGTH, 0);
  glPixelStorei(GL_PACK_SKIP_PIXELS, 0);
  glPixelStorei(GL_PACK_SKIP_ROWS, 0);
  glPixelStorei(GL_PACK_ALIGNMENT, 1);
  int bytes = image_width*image_height*3; //Color space is RGB
  GLubyte *buffer = (GLubyte *)malloc(bytes);

  glFinish();
  glReadPixels(0, 0, image_width, image_height, GL_RGB, GL_UNSIGNED_BYTE, buffer);
  glFinish();
  */

  glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
  int bytes = image_width*image_height*3; //Color space is RGB
  GLubyte *buffer = (GLubyte *)malloc(bytes);
  glReadPixels(0, 0, image_width, image_height, GL_RGB, GL_UNSIGNED_BYTE, buffer);
  CImg<GLubyte> img(buffer,image_width,image_height,1,3,false);
  img.save((name + ".ppm").c_str());
}
