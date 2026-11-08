package org.example;

import java.util.ArrayList;
import java.util.Random;

public class Dungeon {
    public char[][] tiles;
    public ArrayList<Room> rooms = new ArrayList<>();

    public Dungeon(int width, int height, Random random) {
        tiles = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = '#';
            }
        }

        for (int i = 0; i < 200 && rooms.size() < 10; i++) {
            int roomWidth = 5 + random.nextInt(8);
            int roomHeight = 4 + random.nextInt(4);
            int roomX = 1 + random.nextInt(width - roomWidth - 1);
            int roomY = 1 + random.nextInt(height - roomHeight - 1);
            Room room = new Room(roomX, roomY, roomWidth, roomHeight);

            if (overlapsAnyRoom(room)) {
                continue;
            }

            carveRoom(room);
            if (!rooms.isEmpty()) {
                Room previous = rooms.get(rooms.size() - 1);
                carveCorridor(previous.centerX(), previous.centerY(), room.centerX(), room.centerY());
            }
            rooms.add(room);
        }
    }

    private boolean overlapsAnyRoom(Room room) {
        for (Room other : rooms) {
            if (room.intersects(other)) {
                return true;
            }
        }
        return false;
    }

    private void carveRoom(Room room) {
        for (int y = room.y; y < room.y + room.height; y++) {
            for (int x = room.x; x < room.x + room.width; x++) {
                tiles[y][x] = '.';
            }
        }
    }

    private void carveCorridor(int x1, int y1, int x2, int y2) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            tiles[y1][x] = '.';
        }
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            tiles[y][x2] = '.';
        }
    }
}
