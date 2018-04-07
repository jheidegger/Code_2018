#include <Adafruit_NeoPixel.h>
#include <Wire.h>
#ifdef __AVR__

#include <avr/power.h>
#endif
#define PIN            10
#define NUMPIXELS      66

bool connectionLossOn;//Wire
bool powerFailOn;//0
bool swerveFailOn; //1
bool greenOn;
bool elevatorErrorOn;//2
bool whiteBOn;//2
bool pneumaFailOn;//3
bool startupOn;//4
bool intakeErrorOn;//5
bool cubeGetOn;//6
bool openStateOn;//7
int Animation = -1;

Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);

void setup() {
  pixels.begin();
  Wire.begin(9);
  Serial.begin(9600);
  for (int i = 0; i < NUMPIXELS; i++) {
    pixels.setPixelColor(i, pixels.Color(0, 150, 0));
    pixels.show();
    wait(10);
  }
  Wire.onReceive(runAnimation);
  wait(500);
  for (int i = 0; i < NUMPIXELS; i++) {
    pixels.setPixelColor(i, pixels.Color(0, 0, 0));
  }
  pixels.show();
}

void loop() {
  Wire.onReceive(recFunc);//Wire.read()));
  runAnimation();
}

void wait(long x) {
  long currentTime = millis();
  long goTime = millis() + x;
  while (currentTime < goTime) {
    currentTime = millis();
  }
}


void green() {
  for (int i = 0; i < NUMPIXELS; i++ ) {
    pixels.setPixelColor(i, pixels.Color(0, 150, 0));
  }
  pixels.show();
}

void startup() {
  for (int i = 0; i < 6; i++) {
    for (int i = 0; i < NUMPIXELS; i += 2) {
      pixels.setPixelColor(i, pixels.Color(150, 150, 150));
    }
    for (int i = 1; i < NUMPIXELS; i += 2) {
      pixels.setPixelColor(i, pixels.Color(0, 0, 0));
    }
    pixels.show();
    wait(200);
    for (int i = 1; i < NUMPIXELS; i += 2) {
      pixels.setPixelColor(i, pixels.Color(150, 150, 150));
    }
    for (int i = 0; i < NUMPIXELS; i += 2) {
      pixels.setPixelColor(i, pixels.Color(0, 0, 0));
    }
    pixels.show();
    wait(200);
  }
  while (startupOn) {
    for (int x = 0; x < 115;  x++) {
      for (int i = 0; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, 0));
      }
      for (int i = 1; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, (int)(x * .4518), 0));
      }
      for (int i = 2; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, 0));
      }
      for (int i = 3; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, x, 0));
      }
      for (int i = 4; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, 0, x));
      }
      for (int i = 5; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, x));
      }
      for (int i = 6; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, x));
      }
      pixels.show();
    }
    for (int x = 115; x >= 0;  x--) {
      for (int i = 0; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, 0));
      }
      for (int i = 1; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, (int)(x * .4518), 0));
      }
      for (int i = 2; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, 0));
      }
      for (int i = 3; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, x, 0));
      }
      for (int i = 4; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, 0, x));
      }
      for (int i = 5; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, x));
      }
      for (int i = 6; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, x));
      }
      pixels.show();
    }



    for (int x = 0; x < 115; x++) {
      for (int i = 1; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, 0));
      }
      for (int i = 2; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, (int)(x * .4518), 0));
      }
      for (int i = 3; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, 0));
      }
      for (int i = 4; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, x, 0));
      }
      for (int i = 5; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, 0, x));
      }
      for (int i = 6; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, x));
      }
      for (int i = 0; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, x));
      }

      pixels.show();
    }
    for (int x = 115; x >= 0;  x--) {
      for (int i = 1; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, 0));
      }
      for (int i = 2; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, (int)(x * .4518), 0));
      }
      for (int i = 3; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, 0));
      }
      for (int i = 4; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, x, 0));
      }
      for (int i = 5; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, 0, x));
      }
      for (int i = 6; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, x));
      }
      for (int i = 0; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, x));
      }
      pixels.show();
    }



    for (int x = 0; x < 115; x++) {
      for (int i = 2; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, 0));
      }
      for (int i = 3; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, (int)(x * .4518), 0));
      }
      for (int i = 4; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, 0));
      }
      for (int i = 5; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, x, 0));
      }
      for (int i = 6; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, 0, x));
      }
      for (int i = 0; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, x));
      }
      for (int i = 1; i < NUMPIXELS; i += 7) {

        pixels.setPixelColor(i, pixels.Color(x, x, x));
      }
      pixels.show();
    }
    for (int x = 115; x >= 0;  x--) {
      for (int i = 2; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, 0));
      }
      for (int i = 3; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, (int)(x * .4518), 0));
      }
      for (int i = 4; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, 0));
      }
      for (int i = 5; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, x, 0));
      }
      for (int i = 6; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(0, 0, x));
      }
      for (int i = 0; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, 0, x));
      }
      for (int i = 1; i < NUMPIXELS; i += 7) {
        pixels.setPixelColor(i, pixels.Color(x, x, x));
      }
      pixels.show();
    }
  }
}

void powerFail() {
  float delayval = (100);
  while (Animation == 0) {
    for (int i = 0; i < NUMPIXELS; i += 2) {
      pixels.setPixelColor(i, pixels.Color(150, 0, 150));
    }
    for (int i = 1; i < NUMPIXELS; i += 2) {
      pixels.setPixelColor(i, pixels.Color(0, 0, 0));
    }
    pixels.show();
    wait(delayval);

    for (int i = 1; i < NUMPIXELS; i += 2) {
      pixels.setPixelColor(i, pixels.Color(150, 0, 150));
    }
    for (int i = 0; i < NUMPIXELS; i += 2) {
      pixels.setPixelColor(i, pixels.Color(0, 0, 0));
    }
    pixels.show();
    wait(delayval);
  }
}

void whiteB() {
  for (int idx = 0; idx <= 150; idx++) {
    for (int i = 0; i < NUMPIXELS; i++) {
      pixels.setPixelColor(i, pixels.Color(idx, 0, idx));
    }
    pixels.show();
    wait(10);
  }
  for (int idx = 150; idx >= 0; idx--) {
    for (int i = 0; i < NUMPIXELS; i++) {
      pixels.setPixelColor(i, pixels.Color(idx, 0, idx));
    }
    pixels.show();
    wait(10);
  }
}

void fireAnim(){
  
}
void recFunc(int bytes) {
  Animation = Wire.read();
}
void runAnimation() {
  if (Animation == 0)
  {
    powerFail();
  }
  else if (Animation == 1)
  {
    green();
  }

  else if (Animation == 2)
  {
    whiteB();
  }
  else if (Animation == 3){
    fireAnim();
  }
}

