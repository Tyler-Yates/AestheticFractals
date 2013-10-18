#include <iostream>

#include "fbo.h"
#include "libs/CImg-1.5.6/CImg.h"

using namespace cimg_library;

GLuint framebuffer[1];
GLuint status;
GLuint image_width = 1024, image_height = 1024;

void ExternalRenderer::switchToExternalTarget() {
  cout << "test genframebuffers" << endl;
  glGenFramebuffers(1, framebuffer);
  cout << "test" << endl;
  glBindFramebuffer(GL_FRAMEBUFFER, *framebuffer);

  status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
  if (status != GL_FRAMEBUFFER_COMPLETE) {
    fprintf(stderr, "Error generating new frame buffer\n");
  }
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
    fprintf(stderr, "Error generating new render buffer\n");
  }
}

void ExternalRenderer::deleteRenderBuffer(GLuint *renderbuffer) {
  glDeleteRenderbuffers(1, renderbuffer);
}

void convertToNonInterleaved(int w, int h, unsigned char* tangled, unsigned char* untangled) {
  //Take string in format R1 G1 B1 R2 G2 B2... and re-write it 
  //in the format format R1 R2 G1 G2 B1 B2... 
  //Assume 8 bit values for red, green and blue color channels.
  //Assume there are no other channels
  //tangled is a pointer to the input string and untangled 
  //is a pointer to the output string. This method assumes that 
  //memory has already been allocated for the output string.

  int numPixels = w*h;
  int numColors = 3;
  for(int i=0; i<numPixels; ++i) {
    int indexIntoInterleavedTuple = numColors*i;
    //Red
    untangled[i] = tangled[indexIntoInterleavedTuple];
    //Green
    untangled[numPixels+i] = tangled[indexIntoInterleavedTuple+1];
    //Blue
    untangled[2*numPixels+i] = tangled[indexIntoInterleavedTuple+2];
  }
}

void ExternalRenderer::outputToImage(string name) {
  cout << "saving img" << endl;
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

  glBindFramebuffer(GL_FRAMEBUFFER, *framebuffer);
  int bytes = image_width*image_height*3; //Color space is RGB
  GLubyte *buffer = (GLubyte *)malloc(bytes);
  GLubyte *untangled = (GLubyte *)malloc(bytes);
  glReadPixels(0, 0, image_width, image_height, GL_RGB, GL_UNSIGNED_BYTE, buffer);
  convertToNonInterleaved(image_width, image_height, buffer, untangled);
  CImg<GLubyte> img(untangled,image_width,image_height,1,3,false);
  string filename = name + ".ppm";
  img.save(filename.c_str());
  cout << "saved to " + filename << endl;
}
