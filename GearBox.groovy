// import cos


// Cycloid gear disk
double R  = 60;  // pitch radius of fixed ring pins
double E  = 2; // eccentricity offset
double Rr = 10;   // radius of ring pins
double N  = 10;  // number of ring pins

double EN = E/N;

// 100 points on the cycloid
ArrayList<Vector3d> points = []
int segments = 100;

for (int i = 0; i < segments; i += 1) {
    double t = (i * ((2*Math.PI)/segments));
    double x = (R*Math.cos(t))-(Rr*Math.cos(t+Math.atan(Math.sin((1-N)*t)/((R/EN)-Math.cos((1-N)*t)))))-(E*Math.cos(N*t));
    double y = (R*Math.sin(t))-(Rr*Math.sin(t+Math.atan(Math.sin((1-N)*t)/((R/EN)-Math.cos((1-N)*t)))))-(E*Math.sin(N*t));
    points.add(new Vector3d(x, y));
    println("x: " + x + " y: " + y);
    }
       
CSG polygon = Extrude.points(new Vector3d(0,0,10),points);

return polygon;