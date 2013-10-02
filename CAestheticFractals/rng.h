#ifndef __RNG_H__
#define __RNG_H__

#include <iostream>
#include <sstream>
#include <algorithm>
#include <iterator>
#include <stack>
#include <cmath>
#include <fstream>

#include <fcntl.h>

using namespace std;

int rngPrepared = 0;
mt19937 rng;
std::uniform_int_distribution<uint32_t> uint_rand;

void initRNG() {
  ifstream fd("/dev/urandom");
  int seed;
  fd.read(reinterpret_cast<char*>(&seed), sizeof(seed));

  cout << "RNG Seed: " << seed << endl;
  rng.seed(seed);
  rngPrepared = 1;
}

float gen_random_float()
{
  if (!rngPrepared) {
    initRNG();
  }

  return 2*3.1415 * uint_rand(rng) / RAND_MAX - 3.1415;
}

#endif
