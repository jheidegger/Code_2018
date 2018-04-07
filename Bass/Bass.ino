#include <Adafruit_NeoPixel.h>
#include <Wire.h>
#ifdef __AVR__
#include <avr/power.h>
#endif

#define PIN 5
#define NUMPIXELS 66
#define SIDE 26
int breaker = 5;
int x;
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);


void setup() {
  pixels.begin();
  Wire.begin(9);
  for (int i = 0; i < NUMPIXELS; i++) {
    pixels.setPixelColor(i, pixels.Color(0, 150, 0));
    pixels.show();
    wait(10);
  }
  wait(2000);
  for (int i = 0; i < NUMPIXELS; i++) {
    pixels.setPixelColor(i, pixels.Color(0, 0, 0));
  }
  pixels.show();
}

void loop() {
  Wire.onReceive(recFunction);
  Bass();
}
void recFunction(int bytes) {
  x = Wire.read();
}
void wait(long x) {
  long currentTime = millis();
  long goTime = millis() + x;
  while (currentTime < goTime) {
    currentTime = millis();
  }
}
void Bass() {
  if (breaker < (3 * x)) {
    for (breaker; breaker < (3 * x); breaker++) {
      pixels.setPixelColor(breaker, pixels.Color(155, 0, 155));
      pixels.setPixelColor(65 - breaker, pixels.Color(155, 0, 155));
      pixels.show();
      wait(10);
    }
  }

  if (breaker > (3 * x)) {
    for (breaker; breaker >= (3 * x); breaker--) {
      pixels.setPixelColor(breaker, pixels.Color(0, 0, 0));
      pixels.setPixelColor(65 - breaker, pixels.Color(0, 0, 0));
      pixels.show();
      wait(10);
    }
  }
}

