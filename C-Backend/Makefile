###########################################################
# Aesthetic Fractals: Makefile

CC = g++
CFLAGS = -g -std=c++11  # Use -std=c++0x for versions of gcc and g++ <4.7, c++11 otherwise
INCLUDE =
LIBDIR =
LIBS = -lglut -lGLU -lm -lGLEW -lGL -lX11 -lpthread

###########################################################
# Options if compiling on Mac
UNAME := $(shell uname)
ifeq ($(UNAME), Darwin)
CC = g++
CFLAGS = -D__MAC__ -std=c++11 -stdlib=libc++
INCLUDE = 
LIBDIR = -L/usr/X11/lib
LIBS = -framework OpenGL -framework GLUT -lX11
endif

###########################################################
# Uncomment the following line if you are using Mesa
#LIBS = -lglut -lMesaGLU -lMesaGL -lm

###########################################################

aesthetics: main.o expression.o expressionParser.o fractal.o fbo.o
	${CC} ${CFLAGS} $(INCLUDE) -o aesthetics main.o expression.o fractal.o expressionParser.o fbo.o ${LIBDIR} ${LIBS}

main.o: main.cpp expression.h fractal.h common.h
	${CC} -c ${CFLAGS} $(INCLUDE) main.cpp

expression.o: expression.cpp expression.h libs/expressionParser.h
	${CC} -c ${CFLAGS} $(INCLUDE) expression.cpp

fractal.o: fractal.cpp fractal.h vec.h types.h common.h rng.h
	${CC} -c ${CFLAGS} $(INCLUDE) fractal.cpp

expressionParser.o: libs/expressionParser.cpp libs/expressionParser.h
	${CC} -c ${CFLAGS} $(INCLUDE) libs/expressionParser.cpp

fbo.o: fbo.cpp fbo.h common.h
	${CC} -c ${CFLAGS} $(INCLUDE) fbo.cpp

clean:
	rm -f aesthetics *.o *~ *# *.gch
