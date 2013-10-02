#ifndef __FRACTAL_H__
#define __FRACTAL_H__

#include <climits>

#include "expression.h"
#include "vec.h"


class AttractorFractal {
 protected:
  Expression *expressionX;
  Expression *expressionY;

  double minX, minY, maxX, maxY;  
  bool isCalculated;
  vector<Vec3f> points;

 public:
  AttractorFractal() { }

  AttractorFractal(string x, string y) {
    vector<string> vars = {"x", "y"};

    expressionX = new Expression(x, vector<string>(), vars);
    expressionY = new Expression(y, vector<string>(), vars);
    minX = minY = INT_MAX;
    maxX = maxY = INT_MIN;
    calculate();
  }
  
 AttractorFractal(Expression* ex, Expression* ey): expressionX(ex), expressionY(ey) {
    minX = minY = INT_MAX;
    maxX = maxY = INT_MIN;
    calculate();
  }
  
  Vec4d getBounds() { return Vec4d{minX, maxX, minY, maxY }; }
  bool isReady() { return isCalculated; } 
  int getNumPoints() { return points.size(); }
  void calculate();
  void paint();
  
};

class CliffordAttractor : public AttractorFractal {  

 public:
 CliffordAttractor(string x, string y) : AttractorFractal() {
    vector<string> consts = {"a", "b", "c", "d"};
    vector<string> vars = {"x", "y"};
    
    expressionX = new Expression(x, consts, vars);
    expressionY = new Expression(y, consts, vars);
    minX = minY = INT_MAX;
    maxX = maxY = INT_MIN;
    constructConstants();
    calculate();
  }

  void constructConstants();
  void mutateConstants();
};

#endif
