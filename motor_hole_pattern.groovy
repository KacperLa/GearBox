// code here
double width = 17; // mm
double height = 20; // mm

double holeRadius = 3.2/2.0; // mm
double centerHoleRadius = 3; // mm

CSG centerHole = new Cylinder(centerHoleRadius, 1).toCSG();
CSG hole = new Cylinder(holeRadius, 1).toCSG()

return [centerHole,
        hole.movex(width/2.0),
        hole.movey(height/2),
        hole.movex(-width/2.0),
        hole.movey(-height/2.0)];