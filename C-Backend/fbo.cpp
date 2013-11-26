#include <iostream>

#include "fbo.h"
#include "libs/libpng-1.2.50/png.h"

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

bool saveToPNG(string filename, GLubyte *buffer);

void ExternalRenderer::outputToImage(string name) {
  cout << "saving img" << endl;

  glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
  int bytes = image_width*image_height*3; //Color space is RGB
  GLubyte *buffer = (GLubyte *)malloc(bytes);
  GLubyte *untangled = (GLubyte *)malloc(bytes);
  glReadPixels(0, 0, image_width, image_height, GL_RGB, GL_UNSIGNED_BYTE, buffer);
  string filename = name + ".png";

  saveToPNG(filename, buffer);
  free(buffer);
  free(untangled);
  cout << "saved to " + filename << endl;
}

void PNGWriteData(png_structp png_ptr, png_bytep data, png_size_t length)
{
  FILE* fp = (FILE*) png_get_io_ptr(png_ptr);
  fwrite((void*) data, 1, length, fp);
}

bool saveToPNG(string filename, GLubyte *buffer) {
  FILE* out;
  out = fopen(filename.c_str(), "wb");
  if (out == NULL)
    {
      cout << "Can't open screen capture file " << filename.c_str() << endl;
      return false;
    }

  int rowStride = (image_width * 3 + 3) & ~0x3;

  png_bytep* row_pointers = new png_bytep[image_height];
  for (int i = 0; i < image_height; i++)
    row_pointers[i] = (png_bytep) &buffer[rowStride * (image_height - i - 1)];

  png_structp png_ptr;
  png_infop info_ptr;

  png_ptr = png_create_write_struct(PNG_LIBPNG_VER_STRING,
                                    NULL, NULL, NULL);

  if (png_ptr == NULL)
    {
      cout << "Screen capture: error allocating png_ptr" << endl;
      fclose(out);
      return false;
    }

  info_ptr = png_create_info_struct(png_ptr);
  if (info_ptr == NULL)
    {
      cout << "Screen capture: error allocating info_ptr" << endl;
      fclose(out);
      png_destroy_write_struct(&png_ptr, (png_infopp) NULL);
      return false;
    }

  png_set_write_fn(png_ptr, (void*) out, PNGWriteData, NULL);

  png_set_compression_level(png_ptr, 5);
  png_set_IHDR(png_ptr, info_ptr,
               image_width, image_height,
               8,
               PNG_COLOR_TYPE_RGB,
               PNG_INTERLACE_NONE,
               PNG_COMPRESSION_TYPE_DEFAULT,
               PNG_FILTER_TYPE_DEFAULT);

  png_write_info(png_ptr, info_ptr);
  png_write_image(png_ptr, row_pointers);
  png_write_end(png_ptr, info_ptr);

  // Clean up everything . . .
  png_destroy_write_struct(&png_ptr, &info_ptr);
  fclose(out);
}
