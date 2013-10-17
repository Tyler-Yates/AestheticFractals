//GLuint framebuffer;

class ExternalRenderer {
public:
  static void switchToExternalTarget();
  static void switchToWindowTarget();
  static GLuint getNewRenderBuffer();
  static void   deleteRenderBuffer(GLuint renderBuffer);
}
