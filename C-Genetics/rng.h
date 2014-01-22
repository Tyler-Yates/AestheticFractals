#ifndef __RNG_H__
#define __RNG_H__

#include <algorithm>
#include <fstream>
#include <fcntl.h>
#include <random>

using namespace std;

#define PI 3.1415

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

float gen_random_float(float min, float max)
{
  if (!rngPrepared) {
    initRNG();
  }
  
  
  float dist2 = (float)uint_rand(rng) / (float)RAND_MAX;
  if (dist2 > 1)
    dist2 -= 1;

  //  return 2*PI * uint_rand(rng) / RAND_MAX - PI;
  return (max-min) * dist2 + min;
}

#endif
