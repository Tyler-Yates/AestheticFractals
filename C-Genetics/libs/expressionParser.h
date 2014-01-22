#include <algorithm>
#include <string>
#include <vector>
#include <list>
#include <iostream>
#include <fstream>
#include <iterator>  
#include <queue>
#include <stack>
#include <sstream> 
#include <locale>
#include <stdlib.h>
#include <math.h>
#include <set>

int Modulo(int num, int div);
unsigned int OpArgCount( const std::string& s );
int OpPrecedence(const std::string& s);
bool OpLeftAssoc( const std::string& s );
bool IsOperator( const std::string& s );
bool IsComma( const std::string& s );
std::string UpperCase( std::string input );
bool IsPi( const std::string& s );
bool IsE( const std::string& s );
bool IsFunction( std::string s );
bool IsFloat( const std::string& s );                                       
bool IsNumber( const std::string& s );

void addVar( const std::string& s );
bool IsVar( const std::string& s );

void Tokenize( std::list<std::string>& tokens,
               const std::string& text,
               const std::string& delimiter );
bool Execute( const std::vector<std::string>& rpn, std::string& result );
void ReplaceAll( std::string& str, const std::string& from, const std::string& to );
bool InfixToRPN(
                const std::list<std::string>& tokens,
                std::vector<std::string>& inputs );
bool infixStringToRPN( std::string input, std::vector<std::string>* tokens );
