#include <SoftwareSerial.h>
#include <dht.h>
#include "Adafruit_PM25AQI.h"
#include "MHZ19.h"

SoftwareSerial co2Serial(7, 6); // define MH-Z19 RX TX
unsigned long startTime = millis();
SoftwareSerial pmSerial(11, 10);
SoftwareSerial blue(3, 2); // bluetooth module connected here


#define dataPin 8 // Defines pin number to which the sensor is connected
#define tvocPin 7
Adafruit_PM25AQI aqi = Adafruit_PM25AQI();
int CO2;
int tvoc;
dht DHT;
float t = 0; // Gets the values of the temperature
float h = 0; //
int readData;
void setup() {
  Serial.begin(9600); 
  co2Serial.begin(9600);
  pinMode(9, INPUT);
  
  blue.begin(9600);
 
  while (!Serial) delay(10);
  Serial.println("Adafruit PMSA003I Air Quality Sensor");
  pmSerial.begin(9600);
  
  pinMode(tvocPin, OUTPUT);
  digitalWrite(tvocPin, HIGH);
  delay(20*100); //delay 20 seconds
  digitalWrite(tvocPin, LOW);

   if (! aqi.begin_UART(&pmSerial)) { // connect to the sensor over software serial 
    Serial.println("Could not find PM 2.5 sensor!");
    while (1) delay(10);
  }

  Serial.println("PM25 found!");

}
void loop() {
  // read the value at analog input
  readData = DHT.read22(dataPin); // Reads the data from the sensor
  t = DHT.temperature;
  h = DHT.humidity;
  digitalWrite(tvocPin, HIGH);
  tvoc = analogRead(A1);
  digitalWrite(tvocPin, LOW);
  PM25_AQI_Data data;
  
 
  if (! aqi.read(&data)) {
    Serial.println("Could not read from AQI");
    delay(2000);  // try again in a bit!
    return;
  }
  Serial.println("AQI reading success");
  Serial.println();
  Serial.println(F("---------------------------------------"));
  Serial.println(F("Concentration Units (standard)"));
  Serial.println(F("---------------------------------------"));
  Serial.print(F("PM 1.0: ")); Serial.print(data.pm10_standard);
  Serial.print(F("\t\tPM 2.5: ")); Serial.print(data.pm25_standard);
  Serial.print(F("\t\tPM 10: ")); Serial.println(data.pm100_standard);
  Serial.println(F("Concentration Units (environmental)"));
  Serial.println(F("---------------------------------------"));
  Serial.print(F("PM 1.0: ")); Serial.print(data.pm10_env);
  Serial.print(F("\t\tPM 2.5: ")); Serial.print(data.pm25_env);
  Serial.print(F("\t\tPM 10: ")); Serial.println(data.pm100_env);
  Serial.println(F("---------------------------------------"));
  Serial.print(F("Particles > 0.3um / 0.1L air:")); Serial.println(data.particles_03um);
  Serial.print(F("Particles > 0.5um / 0.1L air:")); Serial.println(data.particles_05um);
  Serial.print(F("Particles > 1.0um / 0.1L air:")); Serial.println(data.particles_10um);
  Serial.print(F("Particles > 2.5um / 0.1L air:")); Serial.println(data.particles_25um);
  Serial.print(F("Particles > 5.0um / 0.1L air:")); Serial.println(data.particles_50um);
  Serial.print(F("Particles > 10 um / 0.1L air:")); Serial.println(data.particles_100um);
  Serial.println(F("---------------------------------------"));
 


  Serial.print(t);
  Serial.println("");
  Serial.print(h);
  Serial.println("");
  Serial.print(tvoc);
  Serial.println("");


  blue.println(t);
  blue.print(",");
  blue.print(h);
  blue.print(",");
  blue.print(tvoc);
  blue.print(",");
  Serial.println("------------------------------");
  Serial.print("Time from start: ");
  Serial.print((millis() - startTime) / 1000);
  Serial.println(" s");
  int ppm_uart = readCO2UART();
  int ppm_pwm = readCO2PWM();
  Serial.print("CO2:");
  Serial.print(ppm_pwm);
  delay(5000);
}

int readCO2UART(){
  byte cmd[9] = {0xFF,0x01,0x86,0x00,0x00,0x00,0x00,0x00,0x79};
  byte response[9]; // for answer

  Serial.println("Sending CO2 request...");
  co2Serial.write(cmd, 9); //request PPM CO2

  // clear the buffer
  memset(response, 0, 9);
  int i = 0;
  while (co2Serial.available() == 0) {
//    Serial.print("Waiting for response ");
//    Serial.print(i);
//    Serial.println(" s");
    delay(1000);
    i++;
  }
  if (co2Serial.available() > 0) {
      co2Serial.readBytes(response, 9);
  }
  // print out the response in hexa
  for (int i = 0; i < 9; i++) {
    Serial.print(String(response[i], HEX));
    Serial.print("   ");
  }
  Serial.println("");

  // checksum
  byte check = getCheckSum(response);
  if (response[8] != check) {
    Serial.println("Checksum not OK!");
    Serial.print("Received: ");
    Serial.println(response[8]);
    Serial.print("Should be: ");
    Serial.println(check);
  }
  
  // ppm
  int ppm_uart = 256 * (int)response[2] + response[3];
  Serial.print("PPM UART: ");
  Serial.println(ppm_uart);

  // temp
  byte temp = response[4] - 40;
  Serial.print("Temperature? ");
  Serial.println(temp);

  // status
  byte status = response[5];
  Serial.print("Status? ");
  Serial.println(status); 
  if (status == 0x40) {
    Serial.println("Status OK"); 
  }
  
  return ppm_uart;
}

byte getCheckSum(char *packet) {
  byte i;
  unsigned char checksum = 0;
  for (i = 1; i < 8; i++) {
    checksum += packet[i];
  }
  checksum = 0xff - checksum;
  checksum += 1;
  return checksum;
}

int readCO2PWM() {
  unsigned long th, tl, ppm_pwm = 0;
  do {
    th = pulseIn(9, HIGH, 1004000) / 1000;
    tl = 1004 - th;
    ppm_pwm = 5000 * (th-2)/(th+tl-4);
  } while (th == 0);
  Serial.print("PPM PWM: ");
  Serial.println(ppm_pwm);
  return ppm_pwm;  
}
