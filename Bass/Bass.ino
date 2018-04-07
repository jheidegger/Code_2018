#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif

#define PIN 5
#define NUMPIXELS 66
#define SIDE 26
int breaker = 5;
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);


void setup() {
  pixels.begin();
  for (int i = 0; i < 5; i++) {
    pixels.setPixelColor(i, pixels.Color(155, 0, 155));
  }
  pixels.show();
}

void loop() {
  int up = random (0, 26);
  int down = random (0, 26);
  int x = up;
  int y = down;
  for (breaker; breaker < x; breaker++) {
    if (breaker > 65) {
      breaker = 65;
      break;
    }
    pixels.setPixelColor(breaker, pixels.Color(155, 0, 155));
    pixels.setPixelColor(65-breaker, pixels.Color(155, 0, 155));
    pixels.show();
    delay(10);
  }
  
  for (breaker; breaker >= down; breaker--) {
    if (breaker < 0) {
      breaker = 0;
      break;
    }
    pixels.setPixelColor(breaker, pixels.Color(0, 0, 0));
    pixels.setPixelColor(65-breaker, pixels.Color(0, 0, 0));
    pixels.show();
    delay(10);
  }
}
