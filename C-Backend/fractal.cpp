#include "fractal.h"
#include "rng.h"

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

const int PRECISION_POINTS = 1000000;
const float preAlphaVal = 60000 / (float)PRECISION_POINTS;
const float ALPHA = (preAlphaVal > 1 ? 1 : preAlphaVal);

void AttractorFractal::calculate() {
  isCalculated = false;

  double x, y;
  x = y = 0.0;
  
  vector<double> vals = {x, y};
  for (int i=0; i < PRECISION_POINTS; i++) {
    vals[0] = x;
    vals[1] = y;

    x = expressionX->evaluate(vals);
    y = expressionY->evaluate(vals);
    
    if (x < minX) minX = x;
    if (x > maxX) maxX = x;

    if (y < minY) minY = y;
    if (y > maxY) maxY = y;

    Vec3f p = {(float)x, (float)y, 0};
    
    points.push_back(p);
  }

  bb.min = Vec3f::makeVec(minX, minY, 0);
  bb.max = Vec3f::makeVec(maxX, maxY, 0);
  isCalculated = true;
}

void AttractorFractal::paint() {
  if (!isReady())
    calculate();
  
  glColor4f(1,1,1, ALPHA); 

  glEnableClientState(GL_VERTEX_ARRAY);
  glVertexPointer(3, GL_FLOAT, sizeof(Vec3f), points.data());
  glDrawArrays(GL_POINTS, 0, getNumPoints());
  // deactivate vertex arrays after drawing
  glDisableClientState(GL_VERTEX_ARRAY);

  cout << "drew fractal" << endl;
}

void AttractorFractal::clear() {
  points.clear();
  minX = minY = INT_MAX;
  maxX = maxY = INT_MIN;
}

void AttractorFractal::saveToFile(string name) {
  string fn = name + ".info";
  std::ofstream out(fn);
  std::streambuf *coutbuf = cout.rdbuf();
  cout.rdbuf(out.rdbuf());

  expressionX->print();
  expressionX->printConstants();
  expressionY->print();
  expressionY->printConstants();
  cout.rdbuf(coutbuf);
}

void CliffordAttractor::constructConstants() {
  //  while (1) {
  expressionX->constVals.clear();
  expressionY->constVals.clear();
  
  for (int i = 0; i < expressionX->numConsts; i++) 
    expressionX->constVals.push_back( gen_random_float(-PI, 3*PI) );

  for (int i = 0; i < expressionY->numConsts; i++)
    expressionY->constVals.push_back( gen_random_float(-PI, 3*PI) );

  cout << "ExpressionX: ";
  expressionX->printRPN();
  cout << "ExpressionY: ";
  expressionY->printRPN();

    /* Constants evaluation
    double x = 0;
    double y = 0;

    int good = 100;
    for (int i = 0; i < 100; i++) {
      vector<double> vals{x,y};
      double nx = expressionX->evaluate(vals);
      double ny = expressionY->evaluate(vals);

      double d = pow(x-nx, 2) + pow(y-ny, 2);
      }*/
    //}
}

void CliffordAttractor::mutateConstants() {
  constructConstants();
  clear();
  calculate();
}
