#ifndef __FRACTAL_H__
#define __FRACTAL_H__

#include <climits>

#include "expression.h"
#include "vec.h"
#include "bb.h"

void setPrecisionPoints(int points);

class AttractorFractal {
 protected:
  Expression *expressionX, *expressionY, *expressionZ;
  Expression *expressionR, *expressionG, *expressionB;

  double minX, minY, maxX, maxY, minZ, maxZ;
  BoundingBox bb;
  bool isCalculated;
  vector<Vec3f> points;
  vector<Vec4f> colors;

 public:
  AttractorFractal() { }

  AttractorFractal(string x, string y, string z = "0", string r = "1", string g = "1", string b = "1") {
    vector<string> vars = {"x", "y", "z", "r", "g", "b"};

    expressionX = new Expression(x, vector<string>(), vars);
    expressionY = new Expression(y, vector<string>(), vars);
    expressionZ = new Expression(z, vector<string>(), vars);
    expressionR = new Expression(r, vector<string>(), vars);
    expressionG = new Expression(g, vector<string>(), vars);
    expressionB = new Expression(b, vector<string>(), vars);

    clear();
    calculate();
  }
  
 AttractorFractal(Expression* ex, Expression* ey, Expression* ez = 0, Expression* er = 0, Expression* eg = 0, Expression* eb = 0)
   : expressionX(ex), expressionY(ey), expressionZ(ez), expressionR(er), expressionG(eg), expressionB(eb) {
    clear();
    calculate();
  }

  BoundingBox getbb() { return bb; }
  bool isReady() { return isCalculated; } 
  int getNumPoints() { return points.size(); }
  void calculate();
  void paint();
  void clear();
  void saveToFile(string name);
};

class CliffordAttractor : public AttractorFractal {  
 public:
  vector<string> consts;
  vector<float> constVals;

  CliffordAttractor(string x, string y, string z = "0", string r = "1", string g = "1", string b = "1") : AttractorFractal() {
    consts = {"a", "b", "c", "d"};
    vector<string> vars = {"x", "y", "z", "r", "g", "b"};

    expressionX = new Expression(x, consts, vars);
    expressionY = new Expression(y, consts, vars);
    expressionZ = new Expression(z, consts, vars);
    expressionR = new Expression(r, consts, vars);
    expressionG = new Expression(g, consts, vars);
    expressionB = new Expression(b, consts, vars);

    clear();
    constructConstants();
    calculate();
  }

  void constructConstants();
  void mutateConstants();
};

#endif
