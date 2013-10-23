#include <iostream>

#include "fbo.h"
#include "libs/CImg-1.5.6/CImg.h"

using namespace cimg_library;

int image_width = 1024, image_height = 1024;
GLuint framebuffer;
GLuint status;

void ExternalRenderer::setImageWidth(int width) {
  image_width = width;
}

void ExternalRenderer::setImageHeight(int height) {
  image_height = height;
}

void printFrameBufferError() {
  switch(status) {
  case GL_FRAMEBUFFER_COMPLETE:
    return;
    break;

  case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
    cout << "An attachment could not be bound to frame buffer object!" << endl;
    break;

  case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
    cout << "Attachments are missing! At least one image (texture) must be bound to the frame buffer object!" << endl;
    break;

  case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
    cout << "The dimensions of the buffers attached to the currently used frame buffer object do not match!" << endl;
    break;

  case GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
cout << "The formats of the currently used frame buffer object are not supported or do not fit together!" << endl;
    break;

  case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
cout << "A Draw buffer is incomplete or undefinied. All draw buffers must specify attachment points that have images attached." << endl;
    break;

  case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
cout << "A Read buffer is incomplete or undefinied. All read buffers must specify attachment points that have images attached." << endl;
    break;

  case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
cout << "All images must have the same number of multisample samples." << endl;
    break;

  case GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS :
cout << "If a layered image is attached to one attachment, then all attachments must be layered attachments. The attached layers do not have to have the same number of layers, nor do the layers have to come from the same kind of texture." << endl;;
    break;

  case GL_FRAMEBUFFER_UNSUPPORTED:
cout << "Attempt to use an unsupported format combinaton!" << endl;
break;

 default:
cout << "Unknown error while attempting to create frame buffer object!" << endl;
    break;
  }
}

void ExternalRenderer::switchToExternalTarget() {
  glGenFramebuffers(1, &framebuffer);
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
    fprintf(stderr, "Error while attaching new render buffer:\n");
    printFrameBufferError();
    exit(0);
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

  glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
  int bytes = image_width*image_height*3; //Color space is RGB
  GLubyte *buffer = (GLubyte *)malloc(bytes);
  GLubyte *untangled = (GLubyte *)malloc(bytes);
  glReadPixels(0, 0, image_width, image_height, GL_RGB, GL_UNSIGNED_BYTE, buffer);
  convertToNonInterleaved(image_width, image_height, buffer, untangled);
  CImg<GLubyte> img(untangled,image_width,image_height,1,3,false);
  string filename = name + ".ppm";
  string metafilename = name + ".ppm.info";
  img.save(filename.c_str());

  cout << "saved to " + filename << endl;
}
