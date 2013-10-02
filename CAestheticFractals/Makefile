###########################################################
# Aesthetic Fractals: Makefile

CC = g++
CFLAGS = -g -std=c++11
INCLUDE =
LIBDIR =
LIBS = -lglut -lGLU  -lm

###########################################################
# Options if compiling on Mac
UNAME := $(shell uname)
ifeq ($(UNAME), Darwin)
CC = g++
CFLAGS = -D__MAC__ 
INCLUDE = 
LIBDIR = -L/lusr/X11/lib
LIBS = -framework OpenGL -framework GLUT
endif

###########################################################
# Uncomment the following line if you are using Mesa
#LIBS = -lglut -lMesaGLU -lMesaGL -lm

###########################################################

aesthetics: main.o expression.o expressionParser.o fractal.o
	${CC} ${CFLAGS} $(INCLUDE) -o aesthetics main.o expression.o fractal.o expressionParser.o ${LIBDIR} ${LIBS}

main.o: main.cpp expression.h fractal.h common.h
	${CC} -c ${CFLAGS} $(INCLUDE) main.cpp

expression.o: expression.cpp expression.h libs/expressionParser.h
	${CC} -c ${CFLAGS} $(INCLUDE) expression.cpp

fractal.o: fractal.cpp fractal.h vec.h types.h common.h rng.h
	${CC} -c ${CFLAGS} $(INCLUDE) fractal.cpp

expressionParser.o: libs/expressionParser.cpp libs/expressionParser.h
	${CC} -c ${CFLAGS} $(INCLUDE) libs/expressionParser.cpp

clean:
	rm -f aesthetics *.o *~ *# *.gch