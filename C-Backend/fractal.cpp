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

int PRECISION_POINTS = 1000000;
float preAlphaVal = 90000 / (float)PRECISION_POINTS;
float ALPHA = (preAlphaVal > 1 ? 1 : preAlphaVal);

void setPrecisionPoints(int points) {
  PRECISION_POINTS = points;
  preAlphaVal = 60000 / (float)PRECISION_POINTS;
  ALPHA = (preAlphaVal > 1 ? 1 : preAlphaVal);
}

void AttractorFractal::calculate() {
  isCalculated = false;

  double x, y, z;
  float r, g, b;
  x = y = z = 0.0;
  r = g = b = 1.0;
  
  vector<double> vals = {x, y, z, r, g, b};
  for (int i=0; i < PRECISION_POINTS; i++) {
    vals[0] = x;
    vals[1] = y;
    vals[2] = z;

    x = expressionX->evaluate(vals);
    y = expressionY->evaluate(vals);

    if (expressionZ) {
      z = expressionZ->evaluate(vals);
      if (z < minZ) minZ = z;
      if (z > maxZ) maxZ = z;      
    }
    
    if (x < minX) minX = x;
    if (x > maxX) maxX = x;

    if (y < minY) minY = y;
    if (y > maxY) maxY = y;

    if (expressionR)
      r = expressionR->evaluate(vals);

    if (expressionG) 
      g = expressionG->evaluate(vals);

    if (expressionB) 
      b = expressionB->evaluate(vals);
    

    Vec3f p = {(float)x, (float)y, (float)z};
    Vec4f c = {(float)r, (float)g, (float)b, ALPHA};

    points.push_back(p);
    colors.push_back(c);
  }

  bb.min = Vec3f::makeVec(minX, minY, minZ);
  bb.max = Vec3f::makeVec(maxX, maxY, maxZ);
  isCalculated = true;
}

void AttractorFractal::paint() {
  if (!isReady())
    calculate();

  glEnableClientState(GL_VERTEX_ARRAY);
  glEnableClientState(GL_COLOR_ARRAY);

  glVertexPointer(3, GL_FLOAT, sizeof(Vec3f), points.data());
  glColorPointer(4, GL_FLOAT, sizeof(Vec4f), colors.data());

  glDrawArrays(GL_POINTS, 0, getNumPoints());

  // deactivate vertex arrays after drawing
  glDisableClientState(GL_VERTEX_ARRAY);
  glDisableClientState(GL_COLOR_ARRAY);

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

  printInfo();
  cout.rdbuf(coutbuf);
}

void AttractorFractal::printInfo() {
  cout << "ExpressionX: ";
  expressionX->printInfixString();
  expressionX->printConstants();
  cout << "ExpressionY: ";
  expressionY->printInfixString();
  expressionY->printConstants();
  if (expressionZ) {
    cout << "ExpressionZ: ";
    expressionZ->printInfixString();
    expressionZ->printConstants();
  }
  if (expressionR) {
    cout << "ExpressionR: ";
    expressionR->printInfixString();
    expressionR->printConstants();
  }
  if (expressionG) {
    cout << "ExpressionG: ";
    expressionG->printInfixString();
    expressionG->printConstants();
  }
  if (expressionB) {
    cout << "ExpressionB: ";
    expressionB->printInfixString();
    expressionB->printConstants();
  }
}

void CliffordAttractor::constructConstants() {
  //  while (1) {
  constVals.clear();
  expressionX->constVals.clear();
  expressionY->constVals.clear();
  if (expressionZ) expressionZ->constVals.clear();

  for (int i = 0; i < consts.size(); i++) {
    constVals.push_back(gen_random_float(-PI, 3*PI));
  }
  
  for (int i = 0; i < expressionX->numConsts; i++) 
    expressionX->constVals.push_back( constVals[i] );
  
  for (int i = 0; i < expressionY->numConsts; i++)
    expressionY->constVals.push_back( constVals[i] );
  
  if (expressionZ) {
    for (int i = 0; i < expressionY->numConsts; i++)
      expressionZ->constVals.push_back( constVals[i] );
  }

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
