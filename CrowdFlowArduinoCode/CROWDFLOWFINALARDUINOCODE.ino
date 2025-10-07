#include <WiFiNINA.h>

const char* ssid = "VKTopShotta";
const char* password = "tausifahmed";
WiFiServer server(80);

//sensor stuff
#define IRPinInc A0
#define IRPinDec A1

int population = 0;
bool prevIncState = HIGH;  // Previous state of the increment sensor
bool prevDecState = HIGH;  // Previous state of the decrement sensor


void setup() {
  Serial.begin(9600);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("\nConnected to Wi-Fi!");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  server.begin();

  pinMode(IRPinInc, INPUT);
  pinMode(IRPinDec, INPUT);

}

void loop() {
  bool currentIncState = digitalRead(IRPinInc);
  bool currentDecState = digitalRead(IRPinDec);

  // Check if the increment sensor has transitioned from HIGH to LOW
  if (prevIncState == HIGH && currentIncState == LOW) {
    population++;
  }

  // Check if the decrement sensor has transitioned from HIGH to LOW
  if (prevDecState == HIGH && currentDecState == LOW) {
    if (population > 0) {
      population--;
    }
  }

  // Update the previous states
  prevIncState = currentIncState;
  prevDecState = currentDecState;

  delay(200);

  WiFiClient client = server.available();
  if (client) {
    Serial.println("New client connected.");
    client.println("HTTP/1.1 200 OK");
    client.println("Content-Type: text/plain");
    client.println("Connection: close");
    client.println();
    client.println(population);
    client.stop();
    Serial.println("Client disconnected.");
  }
}
