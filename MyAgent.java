import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import za.ac.wits.snake.DevelopmentAgent;

public class MyAgent extends DevelopmentAgent {
   private int directionToApple = 5;
   private int currentX = 0;
   private int currentY = 0;
   int tailX = 0;
   int tailY = 0;
   private int appleX = 0;
   private int appleY = 0;
   private int mySnakeNum = 0;
   private int[][] grid = new int[50][50];
   private List<int[]> otherSnakesHeads = new ArrayList();
   private List<int[]> zombiesHeads = new ArrayList();

   public void fillGrid() {
      for(int i = 0; i < 50; ++i) {
         for(int j = 0; j < 50; ++j) {
            this.grid[i][j] = 0;
         }
      }

   }

   public void drawLine(String a, String b, int n) {
      String[] startCoords = a.split(",");
      String[] endCoords = b.split(",");
      int x1 = Integer.parseInt(startCoords[0]);
      int y1 = Integer.parseInt(startCoords[1]);
      int x2 = Integer.parseInt(endCoords[0]);
      int y2 = Integer.parseInt(endCoords[1]);
      int dx = Math.abs(x2 - x1);
      int dy = Math.abs(y2 - y1);
      int sx = x1 < x2 ? 1 : -1;
      int sy = y1 < y2 ? 1 : -1;
      int err = dx - dy;

      while(true) {
         if (x1 >= 0 && x1 < 50 && y1 >= 0 && y1 < 50 && this.grid[x1][y1] == 0) {
            this.grid[x1][y1] = n;
         }

         if (x1 == x2 && y1 == y2) {
            return;
         }

         int e2 = err * 2;
         if (e2 > -dy) {
            err -= dy;
            x1 += sx;
         }

         if (e2 < dx) {
            err += dx;
            y1 += sy;
         }
      }
   }

   public void run() {
      try {
         Throwable var1 = null;
         Object var2 = null;

         try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            try {
               String initString = br.readLine();
               String[] temp = initString.split(" ");
               int nSnakes = Integer.parseInt(temp[0]);

               while(true) {
                  while(true) {
                     this.fillGrid();
                     String line = br.readLine();
                     if (line.contains("Game Over")) {
                        System.out.println("log Game over");
                        return;
                     }

                     String[] appleCoords = line.split(" ");
                     if (appleCoords.length >= 2) {
                        this.appleX = Integer.parseInt(appleCoords[0]);
                        this.appleY = Integer.parseInt(appleCoords[1]);
                        this.grid[this.appleX][this.appleY] = 6;
                        int nObstacles = 3;

                        for(int obstacle = 0; obstacle < nObstacles; ++obstacle) {
                           String obs = br.readLine();
                           String[] obsCoords = obs.split(" ");

                           for(int i = 0; i < obsCoords.length - 1; ++i) {
                              this.drawLine(obsCoords[i], obsCoords[i + 1], 7);
                           }
                        }

                        int nZombies = 3;
                        this.zombiesHeads.clear();

                        int k;
                        int headY;
                        int i;
                        String snakeLine;
                        String[] snakeCoords;
                        for(i = 0; i < nZombies; ++i) {
                           snakeLine = br.readLine();
                           snakeCoords = snakeLine.split(" ");
                           k = Integer.parseInt(snakeCoords[0].split(",")[0]);
                           headY = Integer.parseInt(snakeCoords[0].split(",")[1]);
                           int[] temps = new int[]{k, headY};
                           this.zombiesHeads.add(temps);
                           this.grid[k][headY] = 8;

                           for(int i = 0; i < snakeCoords.length - 1; ++i) {
                              this.drawLine(snakeCoords[i], snakeCoords[i + 1], 7);
                           }
                        }

                        this.mySnakeNum = Integer.parseInt(br.readLine());
                        this.otherSnakesHeads.clear();

                        for(i = 0; i < nSnakes; ++i) {
                           snakeLine = br.readLine();
                           if (i == this.mySnakeNum) {
                              snakeCoords = snakeLine.split(" ");
                              this.currentX = Integer.parseInt(snakeCoords[3].split(",")[0]);
                              this.currentY = Integer.parseInt(snakeCoords[3].split(",")[1]);

                              for(k = 3; k < snakeCoords.length - 1; ++k) {
                                 this.drawLine(snakeCoords[k], snakeCoords[k + 1], 9);
                                 if (k == snakeCoords.length - 2) {
                                    this.tailX = Integer.parseInt(snakeCoords[3].split(",")[0]);
                                    this.tailY = Integer.parseInt(snakeCoords[3].split(",")[1]);
                                 }
                              }
                           } else {
                              snakeCoords = snakeLine.split(" ");
                              if (!snakeCoords[0].equals("dead")) {
                                 k = Integer.parseInt(snakeCoords[3].split(",")[0]);
                                 headY = Integer.parseInt(snakeCoords[3].split(",")[1]);
                                 this.grid[k][headY] = 8;
                                 this.otherSnakesHeads.add(new int[]{k, headY});

                                 for(int k = 3; k < snakeCoords.length - 1; ++k) {
                                    this.drawLine(snakeCoords[k], snakeCoords[k + 1], 7);
                                 }
                              }
                           }
                        }

                        i = this.decideMove(this.appleX, this.appleY);
                        System.out.println(i);
                     } else {
                        System.out.println(this.directionToApple);
                     }
                  }
               }
            } finally {
               if (br != null) {
                  br.close();
               }

            }
         } catch (Throwable var27) {
            if (var1 == null) {
               var1 = var27;
            } else if (var1 != var27) {
               var1.addSuppressed(var27);
            }

            throw var1;
         }
      } catch (IOException var28) {
         var28.printStackTrace();
      } catch (NumberFormatException var29) {
         System.out.println("log Error parsing number: " + var29.getMessage());
      }

   }

   private MyAgent.Point bfs(int startX, int startY, int goalX, int goalY) {
      this.ZombieBlock();
      boolean[][] visited = new boolean[50][50];
      Queue<MyAgent.Point> queue = new LinkedList();
      queue.add(new MyAgent.Point(startX, startY, (MyAgent.Point)null));
      visited[startX][startY] = true;
      int[] dx = new int[]{-1, 1, 0, 0};
      int[] dy = new int[]{0, 0, -1, 1};

      while(!queue.isEmpty()) {
         MyAgent.Point current = (MyAgent.Point)queue.poll();
         if (current.x == goalX && current.y == goalY) {
            return current;
         }

         for(int i = 0; i < 4; ++i) {
            int newX = current.x + dx[i];
            int newY = current.y + dy[i];
            if (this.isValidMove(newX, newY) && !visited[newX][newY] && !this.gettingInbetween(newX, newY) && !this.isHeadInZombieArea()) {
               visited[newX][newY] = true;
               queue.add(new MyAgent.Point(newX, newY, current));
            }
         }
      }

      return null;
   }

   public boolean isHeadInZombieArea() {
      int startX = this.currentX - 1;
      int startY = this.currentY - 1;
      int endX = this.currentX + 1;
      int endY = this.currentY + 1;
      Iterator var6 = this.zombiesHeads.iterator();

      int zombieX;
      int zombieY;
      do {
         if (!var6.hasNext()) {
            return false;
         }

         int[] zombieHead = (int[])var6.next();
         zombieX = zombieHead[0];
         zombieY = zombieHead[1];
      } while(zombieX < startX || zombieX > endX || zombieY < startY || zombieY > endY);

      return true;
   }

   public boolean gettingInbetween(int x, int y) {
      if (x - 1 >= 0 && y - 1 >= 0 && x + 1 < 50 && y + 1 < 50) {
         if (this.grid[x + 1][y] == 7 && this.grid[x - 1][y] == 7) {
            return true;
         }

         if (this.grid[x][y - 1] == 7 && this.grid[x][y + 1] == 7) {
            return true;
         }
      }

      return false;
   }

   private int heuristic(int x1, int y1, int x2, int y2) {
      return Math.abs(x1 - x2) + Math.abs(y1 - y2);
   }

   private int decideMove(int appleX, int appleY) {
      if (!this.isClosestToApple()) {
         if (this.moveAlongBody() != -1) {
            return this.moveAlongBody();
         } else {
            return this.escape() != -1 ? this.escape() : this.directionToApple;
         }
      } else {
         MyAgent.Point pathToApple = this.bfs(this.currentX, this.currentY, appleX, appleY);
         if (pathToApple == null) {
            return this.moveAlongBody();
         } else {
            MyAgent.Point current;
            for(current = pathToApple; current.parent != null && current.parent.parent != null; current = current.parent) {
            }

            if (this.otherSnakeNearMe(current.x, current.y)) {
               this.grid[current.x][current.y] = 4;
               return this.decideMove(appleX, appleY);
            } else {
               if (!this.otherSnakeNearMe(current.x, current.y)) {
                  if (current.x < this.currentX) {
                     this.directionToApple = 2;
                  } else if (current.x > this.currentX) {
                     this.directionToApple = 3;
                  } else if (current.y < this.currentY) {
                     this.directionToApple = 0;
                  } else if (current.y > this.currentY) {
                     this.directionToApple = 1;
                  }
               }

               return this.directionToApple;
            }
         }
      }
   }

   private boolean isClosestToApple() {
      int myDistance = this.heuristic(this.currentX, this.currentY, this.appleX, this.appleY);
      int count = 0;
      Iterator var4 = this.otherSnakesHeads.iterator();

      while(var4.hasNext()) {
         int[] head = (int[])var4.next();
         int otherDistance = this.heuristic(head[0], head[1], this.appleX, this.appleY);
         if (otherDistance < myDistance) {
            ++count;
         }
      }

      if (count < 3) {
         return true;
      } else {
         return false;
      }
   }

   private int moveAlongBody() {
      int maxSpace = -1;
      int bestMove = -1;
      int[][] directions = new int[][]{{0, -1, 0}, {0, 1, 1}, {-1, 0, 2}, {1, 0, 3}};
      int[][] var7 = directions;
      int var6 = directions.length;

      for(int var5 = 0; var5 < var6; ++var5) {
         int[] direction = var7[var5];
         int newX = this.currentX + direction[0];
         int newY = this.currentY + direction[1];
         if (this.isValidMove(newX, newY) && !this.gettingInbetween(newX, newY) && !this.otherSnakeNearMe(newX, newY)) {
            int space = this.measureAvailableSpace(newX, newY, direction);
            if (space > maxSpace) {
               maxSpace = space;
               bestMove = direction[2];
            }
         }
      }

      if (bestMove != -1) {
         return bestMove;
      } else {
         Random rand = new Random();
         return rand.nextInt(4);
      }
   }

   private int escape() {
      int[][] corners = new int[][]{new int[2], {0, 49}, {49, 0}, {49, 49}};
      int bestCornerX = -1;
      int bestCornerY = -1;
      int minTraffic = Integer.MAX_VALUE;
      MyAgent.Point pathToApple = this.bfs(this.currentX, this.currentY, this.appleX, this.appleY);
      if (pathToApple != null) {
         int[][] var9 = corners;
         int var8 = corners.length;

         for(int var7 = 0; var7 < var8; ++var7) {
            int[] corner = var9[var7];
            int cornerX = corner[0];
            int cornerY = corner[1];
            int traffic = this.measureTraffic(cornerX, cornerY);
            if (traffic < minTraffic) {
               minTraffic = traffic;
               bestCornerX = cornerX;
               bestCornerY = cornerY;
            }
         }
      }

      MyAgent.Point pathToCorner = this.bfs(this.currentX, this.currentY, bestCornerX, bestCornerY);
      if (pathToCorner != null) {
         MyAgent.Point current;
         for(current = pathToCorner; current.parent != null && current.parent.parent != null; current = current.parent) {
         }

         if (current.x < this.currentX) {
            return 2;
         }

         if (current.x > this.currentX) {
            return 3;
         }

         if (current.y < this.currentY) {
            return 0;
         }

         if (current.y > this.currentY) {
            return 1;
         }
      }

      return -1;
   }

   private int measureTraffic(int x, int y) {
      int traffic = 0;
      int radius = 5;

      for(int i = Math.max(0, x - radius); i < Math.min(50, x + radius); ++i) {
         for(int j = Math.max(0, y - radius); j < Math.min(50, y + radius); ++j) {
            if (this.grid[i][j] != 0) {
               ++traffic;
            }
         }
      }

      return traffic;
   }

   private int measureAvailableSpace(int startX, int startY, int[] direction) {
      int spaceCount = 0;
      int stepsToCheck = 50;

      for(int i = 1; i <= stepsToCheck; ++i) {
         int checkX = startX + direction[0] * i;
         int checkY = startY + direction[1] * i;
         if (!this.isValidMove(checkX, checkY) || this.gettingInbetween(checkX, checkY) || this.otherSnakeNearMe(checkX, checkY)) {
            break;
         }

         ++spaceCount;
      }

      return spaceCount;
   }

   public boolean otherSnakeNearMe(int x, int y) {
      boolean close = false;
      if (x - 1 >= 0 && y - 1 >= 0 && x + 1 < 50 && y + 1 < 50 && (this.grid[x][y - 1] == 8 || this.grid[x][y + 1] == 8 || this.grid[x - 1][y] == 8 || this.grid[x + 1][y] == 8)) {
         close = true;
      }

      return close;
   }

   public void ZombieBlock() {
      int[][] directions = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
      Iterator var3 = this.zombiesHeads.iterator();

      while(var3.hasNext()) {
         int[] head = (int[])var3.next();
         int x = head[0];
         int y = head[1];
         int[][] var9 = directions;
         int var8 = directions.length;

         for(int var7 = 0; var7 < var8; ++var7) {
            int[] direction = var9[var7];
            int nextX = x + direction[0];
            int nextY = y + direction[1];
            if (this.isValidMove(nextX, nextY)) {
               this.grid[nextX][nextY] = 8;
            }
         }
      }

   }

   private boolean isValidMove(int x, int y) {
      return x >= 0 && x < 50 && y >= 0 && y < 50 && (this.grid[x][y] == 0 || this.grid[x][y] == 6);
   }

   public static void main(String[] args) {
      MyAgent agent = new MyAgent();
      start(agent, args);
   }

   class Point {
      int x;
      int y;
      MyAgent.Point parent;

      Point(int x, int y, MyAgent.Point parent) {
         this.x = x;
         this.y = y;
         this.parent = parent;
      }
   }
}
