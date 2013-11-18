#ifndef __FRACTAL_H__
#define __FRACTAL_H__

#include <climits>

#include "expression.h"
#include "vec.h"
#include "bb.h"

void setPrecisionPoints(int points);

class AttractorFractal {
 protected:
  Expression *expressionX;
  Expression *expressionY;
  Expression *expressionZ;

  double minX, minY, maxX, maxY, minZ, maxZ;
  BoundingBox bb;
  bool isCalculated;
  vector<Vec3f> points;
  vector<Vec4f> colors;

 public:
  AttractorFractal() { }

  AttractorFractal(string x, string y, string z = "") {
    vector<string> vars = {"x", "y", "z"};

    expressionX = new Expression(x, vector<string>(), vars);
    expressionY = new Expression(y, vector<string>(), vars);
    if (z != "")
      expressionZ = new Expression(z, vector<string>(), vars);
    else
      expressionZ = 0;

    clear();
    calculate();
  }
  
 AttractorFractal(Expression* ex, Expression* ey, Expression* ez = 0): expressionX(ex), expressionY(ey) {
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

 CliffordAttractor(string x, string y, string z = "") : AttractorFractal() {
    consts = {"a", "b", "c", "d"};
    vector<string> vars = {"x", "y", "z"};

    expressionX = new Expression(x, consts, vars);
    expressionY = new Expression(y, consts, vars);
    if (z != "")
      expressionZ = new Expression(z, consts, vars);
    else
      expressionZ = 0;

    clear();
    constructConstants();
    calculate();
  }

  void constructConstants();
  void mutateConstants();
};

#endif
