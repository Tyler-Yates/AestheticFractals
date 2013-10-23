#include <iostream>
#include <sstream>
#include <algorithm>
#include <iterator>
#include <stack>
#include <cmath>

#include <fcntl.h>

#include "libs/expressionParser.h"
#include "expression.h"

using namespace std;

Expression::Expression(string infixExpression, vector<string> consts, vector<string> vars) {
  infixString = infixExpression;

  root = NULL;
  vector<string> rpnTokens;

  this->vars = vars;
  this->numVars = vars.size();
  for (string s: vars)
    addVar(s);
  
  this->consts = consts;
  this->numConsts = consts.size();
  for (string s: consts)
    addVar(s);

  infixStringToRPN(infixExpression, &rpnTokens);
  createTree(rpnTokens);
}

void Expression::createTree(vector<string> tokens) {
  stack<Node*> stack;

  for (vector<string>::iterator itr = tokens.begin(); itr != tokens.end(); ++itr) {
    Node *n = new Node(*itr);
    if (n->isBinaryOp()) {
      Node *right = stack.top(); stack.pop();
      Node *left = stack.top(); stack.pop();
      n->setLeft(left);
      n->setRight(right);
    } else if (n->isUnaryOp()) {
      Node *right = stack.top(); stack.pop();
      n->setRight(right);
    }

    stack.push(n);
  }

  root = stack.top(); stack.pop();
}

double Expression::evaluate(vector<double> values) {
  return evalTree(root, values);
}

double Expression::evalTree(Node *n, vector<double> values) {
  if (!n) return -1;

  string token = n->getValue();
  if (n->isLeaf()) {
    for (int i = 0; i < numVars; i++) {
      if (token == vars[i]) 
        return values[i];
    }
    for (int i = 0; i < numConsts; i++) {
      if (token == consts[i])
        return constVals[i];
    }
    
    return stod(token);
  }
  
  if (n->isBinaryOp()) {
    if (token == "+")      return evalTree(n->getLeft(), values) + evalTree(n->getRight(), values);
    else if (token == "-") return evalTree(n->getLeft(), values) - evalTree(n->getRight(), values);
    else if (token == "/") return evalTree(n->getLeft(), values) / evalTree(n->getRight(), values);
    else if (token == "*") return evalTree(n->getLeft(), values) * evalTree(n->getRight(), values);
    else if (token == "^") return pow(evalTree(n->getLeft(), values), evalTree(n->getRight(), values));
  } else if (n->isUnaryOp()) {
    if (token == "sin")       return sin(evalTree(n->getRight(), values));
    else if (token == "cos")  return cos(evalTree(n->getRight(), values));
    else if (token == "tan")  return tan(evalTree(n->getRight(), values));
    else if (token == "abs")  return abs(evalTree(n->getRight(), values));
  }

  return 0.0;
}

void Expression::printInfixString() {
  cout << infixString << endl;
}

void Expression::printConstants() {
  for (int i = 0; i < numConsts && i < constVals.size(); i++)  {
    cout << consts[i] << " = " << constVals[i] << endl;    
  }
}

void Expression::print() { 
  printTree(root); 
  cout << endl; 
  printConstants();
}
void Expression::printRPN() {
  printTreeRPN(root); 
  cout << endl; 
  printConstants();
}

void Expression::printTree(Node *n) {
  if (!n) return;
  
  printTree(n->getLeft());
  cout << n->getValue() << " ";
  printTree(n->getRight());
}

void Expression::printTreeRPN(Node *n) {
  if (!n) return;
  
  printTreeRPN(n->getLeft());
  printTreeRPN(n->getRight());
  cout << n->getValue() << " ";
}
