#include <set>
#include "expressionParser.h"

const double pi = 3.1415927;
const double e = 2.71828182846;

static std::set<std::string> *vars;

int Modulo(int num, int div)
{
   int mod = num % div;
   
   return ( num >= 0 || mod == 0 ) ? mod : div + mod;
}


unsigned int OpArgCount( const std::string& s )
{
    unsigned int val = 1;
    
    if ( s == "*" || s == "/" || s == "%" || 
         s == "+" || s == "-" || s == "=" || 
         s == "^" || s == "POW" )
    {
        val = 2;
    }
    else if ( s == "!" )
    {
        val = 1;
    }   
    
    return val;
}


// Return operator precedence
// precedence   operators       associativity
// 4            !               right to left
// 3            * / %           left to right
// 2            + -             left to right
// 1            =               right to left
int OpPrecedence(const std::string& s)
{
    int precedence = 1;
    
    if ( s == "!" )
    {
        precedence = 4;
    }
    else if ( s == "*" || s == "/" || s == "%" )
    {
        precedence = 3;
    }
    else if ( s == "+" || s == "-" )
    {
        precedence = 2;
    }
    else if ( s == "=" )
    {
        precedence = 1;        
    }
    
    return precedence;
}
 
// Return true if left associative; false otherwise
bool OpLeftAssoc( const std::string& s )
{
    bool opLeftAssoc = false;
    
    // left to right
    if ( s == "*" || s == "/" || s == "%" || s == "+" || s == "-" )
    {
        opLeftAssoc = true;
    }
    // right to left
    else if ( s == "=" || s == "!" )
    {
        opLeftAssoc = false;
    }
   
    return opLeftAssoc;
}

// Is token an operator
bool IsOperator( const std::string& s )
{
    return s == "+" || s == "-" || s == "/" || 
           s == "*" || s == "!" || s == "%" || 
           s == "=";
}

// Is token a function argument separator eg comma
bool IsComma( const std::string& s )
{
    return s == ",";    
}

// Convert string into all uppercase
std::string UpperCase( std::string input ) 
{
  for ( std::string::iterator it = input.begin(); 
        it != input.end();
        ++it )
  {
    *it = toupper(*it);
  }

  return input;
}

// Is token PI
bool IsPi( const std::string& s )
{
    bool isPi = false;
    
    if ( UpperCase( s ) == "PI" ) 
    {
        isPi = true;
    }
    
    return isPi;
}

// Is token Euler's constant
bool IsE( const std::string& s )
{
    bool isE = false;
    
    if ( UpperCase( s ) == "E" ) 
    {
        isE = true;
    }
    
    return isE;
}

// Is the token a function
bool IsFunction( std::string s )
{
    std::string str = UpperCase( s );
    
    bool isFunction = false;   
    
    if ( str.find( "^" )   != std::string::npos ||
         str.find( "SIN" ) != std::string::npos ||
         str.find( "COS" ) != std::string::npos ||
         str.find( "TAN" ) != std::string::npos ||
         str.find( "LN"  ) != std::string::npos ||
         str.find( "LOG" ) != std::string::npos ||
         str.find( "EXP" ) != std::string::npos ||
         str.find( "POW" ) != std::string::npos ||
         str.find( "SQRT" ) != std::string::npos )
    {
        isFunction = true;
    }
    
    return isFunction;
}

// Is the number a float
bool IsFloat( const std::string& s ) 
{
    std::istringstream iss( s );
    float f;
    iss >> std::noskipws >> f; 
    return iss.eof() && !iss.fail(); 
}

// Is the string a number
bool IsNumber( const std::string& s )
{
    std::string::const_iterator it = s.begin();
    while (it != s.end() && std::isdigit(*it, std::locale() ) ) 
    {
        ++it;
    }
    
    return !s.empty() && it == s.end();
}

void addVar( const std::string& s ) {
  if (!vars) vars = new std::set<std::string>();
  vars->insert(s);
}

bool IsVar ( const std::string& s ) {
  if (!vars) vars = new std::set<std::string>();
  return vars->find( s ) != vars->end();
}

// Split selected text into delimited vector array of strings
void Tokenize( std::list<std::string>& tokens,    
               const std::string& text,    
               const std::string& delimiter )    
{    
    size_t next_pos = 0;    
    size_t init_pos = text.find_first_not_of( delimiter, next_pos );    
        
    while ( next_pos != std::string::npos &&    
            init_pos != std::string::npos )    
    {    
        // Get next delimiter position    
        next_pos = text.find( delimiter, init_pos );    
        
        std::string token = text.substr( init_pos, next_pos - init_pos );    
        tokens.push_back( token );       
        
        init_pos = text.find_first_not_of( delimiter, next_pos );    
    }    
}  


// Deduce the numerical result from the RPN string passed to it
// http://en.wikipedia.org/wiki/Reverse_Polish_notation#Postfix_algorithm
bool Execute( const std::vector<std::string>& rpn, std::string& result ) 
{
    typedef std::vector<std::string>::const_iterator rpn_iter;
    std::stack<std::string> stack;
    
    // While there are input tokens left
    for ( rpn_iter it = rpn.begin(); it != rpn.end(); it++ )
    {
        // Read the next token from input.
        std::string token = *it;
                
        // If the token is a value push it onto the stack.
        if( IsNumber( token ) || 
            IsFloat( token )  || 
            IsPi( token )     || 
            IsE( token ) )   
        {  
            if ( IsPi( token ) )
            {
                std::stringstream s;
                s << pi;
                token = s.str();
            }
            else if ( IsE( token ) )
            {
                std::stringstream s;
                s << e;
                token = s.str();
            }
            stack.push( token );
        }
        
        // Otherwise, the token is an operator or a function
        else if( IsOperator( token ) || IsFunction( token ) )            
        {           
            // It is known a priori that the operator takes n arguments.
            unsigned int nargs = OpArgCount( UpperCase( token ) );
            
            // TODO If there are fewer than n values on the stack
            unsigned int stackArgs = stack.size();
            if( stackArgs < nargs) 
            {
                // (Error) The user has not input sufficient values in the expression.
                return false;
            }
            // Else, Pop the top n values from the stack.
            std::vector<double> args;
            while ( nargs > 0 )
            {
                std::string value = stack.top();
                double d = strtod( value.c_str(), NULL );  
                args.push_back( d );
                stack.pop();
                nargs--;                
            }
            
            double result = 0.0;
            double iresult = 0;
            if ( IsOperator( token ) )
            {
                // Token is an operator: pop top two entries                          
                double d2 = args[ 0 ];   
                double d1 = args[ 1 ];     

                //Get the result  
                if ( token == "+" )
                {
                    result = d1 + d2;
                }
                else if ( token == "-" )
                {
                    result = d1 - d2;
                }
                else if ( token == "*" )
                {
                    result = d1 * d2;
                }
                else if ( token == "/" )
                {
                    result = d1 / d2;
                }
                else if ( token == "%" )                 
                {
                    int i2 = (int) args[ 0 ];   
                    int i1 = (int) args[ 1 ]; 
                    iresult = Modulo( i1, i2 );
                    result = iresult;
                }               
            }
            else if ( IsFunction( token ) )
            {
                double d0 = args[ 0 ]; 
                
                std::string capToken = UpperCase( token );
                
                // If say -SIN( x ) then multiply result of SIN by -1.0
                double mult = 
                        token.find( "-" ) != std::string::npos ? -1 : 1;
                                
                if ( capToken.find( "SIN" ) != std::string::npos )
                {                    
                    result = sin( d0 );                    
                }
                else if ( capToken.find( "COS" ) != std::string::npos )
                {                  
                    result = cos( d0 );                    
                }    
                else if ( capToken.find( "TAN" ) != std::string::npos )
                {                  
                    result = tan( d0 );                    
                }  
                else if ( capToken.find( "LN" ) != std::string::npos )
                {                  
                    result = log( d0 );                    
                }   
                else if ( capToken.find( "LOG" ) != std::string::npos )
                {                  
                    result = log10( d0 );                    
                }   
                else if ( capToken.find( "EXP" ) != std::string::npos )
                {                  
                    result = exp( d0 );                    
                }                   
                else if ( capToken.find( "^" ) != std::string::npos )                 
                { 
                    double d2 = args[ 0 ];   
                    double d1 = args[ 1 ];  
                    result = pow( d1, d2);
                }
                else if ( capToken.find( "POW" ) != std::string::npos )
                {                  
                    double d2 = args[ 0 ];   
                    double d1 = args[ 1 ];  
                    result = pow( d1, d2);             
                } 
                else if ( capToken.find( "SQRT" ) != std::string::npos )
                {                  
                    result = sqrt( d0 );                    
                }      
                
                result *= mult;
            }                       
                        
            // Push the returned results, if any, back onto the stack
            // Push result onto stack   
            if ( result == (long) result )
            {
                result = long( result );
            }
            std::stringstream s;  
           s << result;  
            stack.push( s.str() );                
        }               
    }
   
    // If there is only one value in the stack then
    // that value is the result of the calculation.
    if ( stack.size() == 1 )
    {
       result = stack.top(); 
       return true;
    }
    
    // If there are more values in the stack
    // (Error) The user input has too many values.
    return false;
}

// Replace all instances of selected string with replacement string
void ReplaceAll( std::string& str, const std::string& from, const std::string& to ) 
{
    size_t start_pos = 0;
    
    while( (start_pos = str.find(from, start_pos)) != std::string::npos) 
    {
         str.replace(start_pos, from.length(), to);
         start_pos += to.length(); // ...
    }
}

// Convert infix expression format into reverse Polish notation    
bool InfixToRPN( 
        const std::list<std::string>& tokens,         
        std::vector<std::string>& inputs )    
{

    typedef std::list<std::string>::const_iterator tok_iter;
    std::stack<std::string> stack;
    std::queue<std::string> outputQueue;
    
    bool success = true;
    
    // For each token
    for ( tok_iter it = tokens.begin(); it != tokens.end(); it++ )
    {
        std::string token = *it;
   
        // If token is a number add it to the output queue
        if ( IsNumber( token ) || IsFloat( token ) || IsVar( token ) || IsPi( token ) || IsE( token ) )
        {
            outputQueue.push( token );
        }
        else if ( IsFunction( token ) )
        {
            stack.push( token );
        }
        else if ( IsComma( token ) )
        {
            // Until token at the top of stack is left parenthesis, pop operators 
            // off the stack onto the output queue.             
            std::string stackToken = stack.top();            
            
            while ( stackToken != "(" )
            {
                outputQueue.push( stackToken );
                stack.pop();
                stackToken = stack.top();
            }
            
            if ( stackToken == "(" )
            {
                success = true;
            }  
            else
            {
                success = false;
            }
        }
        else if ( IsOperator( token ) )
        {
            // While there is operator token, o2, at the top of the stack, 
            // and either o1 is left-associative and its precedence is less than 
            // or equal to that of o2, or o1 has precedence less than that of o2
            while( !stack.empty() && IsOperator( stack.top() ) && 
                   ( ( OpLeftAssoc( token ) && OpPrecedence( token ) <= OpPrecedence( stack.top() ) ) ||  
                     ( OpPrecedence( token ) < OpPrecedence( stack.top() ) ) ) )
            {
                // Pop o2 off the stack, onto the output queue
                std::string stackToken = stack.top();       
                stack.pop();
                outputQueue.push( stackToken );
            }

            // Push o1 onto the stack
            stack.push( token );

        }
        else if ( token == "(" )    
        {    
            // Push token to top of the stack  
            stack.push(token);    
        } 
        else if ( token == ")" )    
        { 
            // Until the token at the top of the stack is a left parenthesis, pop operators 
            // off the stack onto the output queue.
            while ( !stack.empty() && stack.top() != "(" )    
            {    
                // Add to end of list  
                outputQueue.push( stack.top() );    
                stack.pop();  
            }  
            
            // If stack runs out without finding a left parenthesis, 
            // there are mismatched parentheses
            if ( !stack.empty() )
            {
                std::string stackToken = stack.top(); 
            
                // Mismatched parentheses
                if ( stackToken != "(" )
                {                    
                    success = false;
                }                
            }
            else
            {
                return false;
            }
                        
            // Pop left parenthesis from the stack, but not onto output queue.
            stack.pop();  
            
            // If token at top of stack is function token, pop it onto output queue
            if ( !stack.empty() )
            {
                std::string stackToken = stack.top();  
                if ( IsFunction( stackToken ) )
                {
                    outputQueue.push( stackToken ); 
                    stack.pop();
                }    
            }
        }                                 
    }

    // While there are still operator tokens in the stack:
    while ( !stack.empty() )    
    {   
        // Pop the operator onto the output queue
        outputQueue.push( stack.top() );    
        stack.pop();  
    } 
    
    while ( !outputQueue.empty() )
    {
      std::string token = outputQueue.front();
      inputs.push_back( token );
      outputQueue.pop();
    }  

    return success;
}

bool infixStringToRPN( std::string input, std::vector< std::string > *inputs ) {
    std::list< std::string > tokens;
    /*
    size_t nLeft  = std::count( input.begin(), inputs.end(), '(');
    size_t nRight = std::count( input.begin(), inputs.end(), ')');
    
    // Check left and right parentheses are equal
    if ( nLeft != nRight )
    {
        std::cout << "Error: mismatched parentheses" << std::endl;
        return false;
    }
    */
    // Insert whitepaces before and after each special characters
    std::string charSet[] = { "(", ")", "%", "+", "-", "*", "/", "^", "," };
    size_t size = sizeof( charSet ) / sizeof( std::string );
    
    for ( int i = 0; i < size; i++ )
    {
        std::string s = charSet[ i ];
        ReplaceAll( input, s, " " + s + " " );
    }       
    
    Tokenize( tokens, input, " " );
    
    // Deal with start token being a minus sign
    std::string firstToken = tokens.front();
    if ( firstToken == "-" )
    {
        std::list<std::string>::const_iterator it = tokens.begin();
        it++;
        std::string nextToken = *( it );
        
        if ( IsNumber( nextToken ) || IsFloat( nextToken ) || IsVar( nextToken ) )
        {
            tokens.pop_front();
            tokens.front() = firstToken + nextToken;
        }                
    }
    
    // Deal with minus sign after opening parenthesis or operator
    typedef std::list<std::string>::iterator t_iter;
    std::string prevToken = "";   
    for ( t_iter it = tokens.begin(); it != tokens.end(); it++ )
    {
        std::string token = *it;
        
        std::list<std::string>::iterator nit = it;
        std::advance ( nit, 1 ); 
        
        if ( nit == tokens.end() )
        {
            break;
        }
        
        std::string ntoken = *nit;
        
        if ( token == "-" && prevToken == "(" )
        {           
          if ( IsNumber( ntoken ) || IsFloat( ntoken ) || IsVar( ntoken ) )
            {
                tokens.erase( nit );
                *it = "-" + ntoken;
            }       
        }
        
        if ( token == "-" && 
            ( IsOperator( prevToken ) || prevToken == "^" || prevToken == "%" ) )
        {           
          if ( IsNumber( ntoken ) || IsFloat( ntoken ) || IsVar( ntoken ) || IsFunction( ntoken ) )
            {
                tokens.erase( nit );
                *it = "-" + ntoken;
            }       
        }
              
        prevToken = token;
    }
    
    // Deal with minus sign before opening parenthesis
    prevToken = "";   
    t_iter prevIt;
    
    for ( t_iter it = tokens.begin(); it != tokens.end(); it++ )
    {
        std::string token = *it;
        
        if ( token == "(" && prevToken == "-" )
        {                  
            *prevIt = "-1";
            tokens.insert( it, "*" );
        }
        
        prevToken = token;
        prevIt = it;        
    }
            
    if ( InfixToRPN( tokens, *inputs ) ) {
      // Error handler
    }

    return true;
}
/*
int main(int argc, char** argv) 
{    
    std::string result;
  
    //std::string originalInput = "11 ^ -7";
    //    std::string originalInput = "1*- sin( Pi / 2)";
    //std::string originalInput = "-8 + 5";
    //std::string originalInput = "5 + (-1 + 2 )";
    //std::string originalInput = "cos ( ( 1.3 + 1 ) ^ ( 1 / 3 ) ) - log ( -2 * 3 / -14 )";
    //std::string originalInput = "cos ( ( 1.3 + 1 ) ^ ( 1 / 3 ) )";
    //std::string originalInput = "( 1.3 + 1 ) ^ ( 1 / 3 )";
    //std::string originalInput = "cos( 1.32001)";
    //std::string originalInput = "-8+3";
    //std::string originalInput = "ln(2)+3^5";
    //std::string originalInput = "((2*(6-1))/2)*4";
    //std::string originalInput = "PI*pow(9/2,2)";
    //std::string originalInput = "PI*pow(9/-2,2)";
    //std::string originalInput = "pow(2, 3)";
    //std::string originalInput = "3/2 + 4*(12+3)";
    //std::string originalInput = "( 1 + 2 ) * ( 3 / 4 ) ^ ( 5 + 6 )";
    //std::string originalInput = "5 + ((1 + 2) * 4) - 3";
    //std::string originalInput = "34.5*(23+1.5)/2";
    //std::string originalInput = "sin( cos( 90 * pi / 180  ) )";
    //std::string originalInput = "exp( 1.11 )";
    //std::string originalInput = "5 + ((1 + 2) * 4) - 3 ";
    //std::string originalInput = "3 + 4 * 2 / ( 1 - 5 ) ^ 2 ^ 3";  
    //std::string originalInput = "2.5^3";
    //std::string originalInput = "cos(1/20)+sin(1/30)+cos(1/50)";
    //std::string originalInput = "SQRT(4 )";    
    //std::string originalInput = "5 + (-1 + 2 )";
    //std::string originalInput = "-(2+3)*(4*10-1)+100";
    //std::string originalInput = "(2+3)*-(4*10-1)+100";
    
    std::string input = originalInput;
    
    std::list< std::string > tokens;
    std::vector< std::string > inputs;
    
    size_t nLeft  = std::count( input.begin(), input.end(), '(');
    size_t nRight = std::count( input.begin(), input.end(), ')');
    
    // Check left and right parentheses are equal
    if ( nLeft != nRight )
    {
        std::cout << "Error: mismatched parentheses" << std::endl;
        return 1;
    }
    
    // Insert whitepaces before and after each special characters
    std::string charSet[] = { "(", ")", "%", "+", "-", "*", "/", "^", "," };
    size_t size = sizeof( charSet ) / sizeof( std::string );
    
    for ( int i = 0; i < size; i++ )
    {
        std::string s = charSet[ i ];
        ReplaceAll( input, s, " " + s + " " );
    }       
    
    Tokenize( tokens, input, " " );
    
    // Deal with start token being a minus sign
    std::string firstToken = tokens.front();
    if ( firstToken == "-" )
    {
        std::list<std::string>::const_iterator it = tokens.begin();
        it++;
        std::string nextToken = *( it );
        
        if ( IsNumber( nextToken ) || IsFloat( nextToken ) )
        {
            tokens.pop_front();
            tokens.front() = firstToken + nextToken;
        }                
    }
    
    // Deal with minus sign after opening parenthesis or operator
    typedef std::list<std::string>::iterator t_iter;
    std::string prevToken = "";   
    for ( t_iter it = tokens.begin(); it != tokens.end(); it++ )
    {
        std::string token = *it;
        
        std::list<std::string>::iterator nit = it;
        std::advance ( nit, 1 ); 
        
        if ( nit == tokens.end() )
        {
            break;
        }
        
        std::string ntoken = *nit;
        
        if ( token == "-" && prevToken == "(" )
        {           
            if ( IsNumber( ntoken ) || IsFloat( ntoken ) )
            {
                tokens.erase( nit );
                *it = "-" + ntoken;
            }       
        }
        
        if ( token == "-" && 
            ( IsOperator( prevToken ) || prevToken == "^" || prevToken == "%" ) )
        {           
            if ( IsNumber( ntoken ) || IsFloat( ntoken ) || IsFunction( ntoken ) )
            {
                tokens.erase( nit );
                *it = "-" + ntoken;
            }       
        }
              
        prevToken = token;
    }
    
    // Deal with minus sign before opening parenthesis
    prevToken = "";   
    t_iter prevIt;
    
    for ( t_iter it = tokens.begin(); it != tokens.end(); it++ )
    {
        std::string token = *it;
        
        if ( token == "(" && prevToken == "-" )
        {                  
            *prevIt = "-1";
            tokens.insert( it, "*" );
        }
        
        prevToken = token;
        prevIt = it;        
    }
    
    for ( t_iter it = tokens.begin(); it != tokens.end(); it++ )
    {
        std::cout << *it << std::endl;    
    }
        
            
    if ( InfixToRPN( tokens, inputs ) )
    { 
       Execute( inputs, result ); 
       
       // Output the result
       double res = strtod( result.c_str(), NULL );  
       if ( res == (long) res )
       {
           long lres = (long) res;           
           std::stringstream s;
           s << lres;
           result = s.str();                
       }
          
       std::cout << std::endl;
       std::cout << originalInput << " = " << result << std::endl;
    }
    else
    {
        std::cout << "Error: mismatched parentheses" << std::endl;
    }                    

    return 0;
}
*/
