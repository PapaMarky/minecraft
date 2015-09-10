// Copyright 2015, Mark Dyer

WorldDisplay world_display = null;
void setup(){
  size (1024, 960);
  surface.setResizable(true);
  surface.setSize(2 * displayWidth / 3, 2 * displayHeight / 3);

  world_display = new WorldDisplay(this);
  world_display.setup();
}

void keyTyped() {
  world_display.keyTyped();
}

void draw() {
  world_display.draw();
}