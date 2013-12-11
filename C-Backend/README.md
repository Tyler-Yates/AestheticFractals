Genetic Fractals
=================

A research tool studying the generation and evolution of aesthetically-pleasing fractals. It uses crowd-sourced testing along with observational patterns in the mathematical structure of attractor fractals to find optimal constants and equations.

This program is meant to be used in conjunction with Tyler Yates' [Java Evolution GUI](https://bitbucket.org/terminex9/aestheticfractals) (Must request access to view).

### Dependencies

        sudo apt-get install libglu1-mesa-dev freeglut3-dev mesa-common-dev libglew-dev libpng-dev

Usage
============

### Rendering Mode

To open the single-fractal viewer with a randomized Clifford Attractor:

        make
        ./aesthetics

You can also provide optional infix equation representations with vars {x,y,z}, consts {a,b,c,d}.

        ./aesthetics [EXPR_X EXPR_Y EXPR_Z EXPR_R EXPR_G EXPR_B]
        ./aesthetics "sin(-1.4 * y) + cos(-1.4 * x)" "sin(1.6 * x) + 0.7 * cos(1.6 * y)" "x" "x" "y" "z"

Controls:
* f - Toggle Fullscreen
* s - Save image to test.ppm

### Image Save Mode

In addition to rendering mode, you can call the program to draw and save fractals offscreen with optional parameters:

        ./aesthetics -save [ -p PRECISION_POINTS ] [ -s WIDTH HEIGHT ] IMG_NAME EXPR_X EXPR_Y EXPR_Z EXPR_R EXPR_G EXPR_B

-------------------------------

Visit https://bitbucket.org/terminex9/aestheticfractals for the most up-to-date version of our project, including the Java GUI.

Kevin Yeh and Tyler Yates © 2013 (UT Freshman Research Initiative: Computational Intelligence)
