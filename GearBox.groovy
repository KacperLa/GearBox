// import cos

// GearBox
double Rg = 32.5; // Radius of Gear box

// Cycloid gear disk
double R  = 27;  // pitch radius of fixed ring pins
double E  = .75;   // eccentricity offset
double Rr = 2.5;   // radius of ring pins
double N  = 10;  // number of ring pins

double Cr = 23.0/2.0; // disk center shaft radius
double Dr = 2.5;    // drive pin radius
double Nd = 6;    // number of drive pins
double Pd = 17;  // pitch radius of drive pins

double DCr = 17.0/2.0; // Drive Cam radius

double SA = 6; // Drive Shaft apatrum radius

double LargeBearingR = 58/2.0; // Large bearing radius
double largeBearingH = 7; // Large bearing height
double largeBearingInnerR = 45/2.0; // Large bearing inner radius
double EN = E/N;

// 100 points on the cycloid
ArrayList<Vector3d> points = []
int segments = 150;

for (int i = 0; i < segments; i += 1) {
    double t = (i * ((2*Math.PI)/segments));
    double x = (R*Math.cos(t))-(Rr*Math.cos(t+Math.atan(Math.sin((1-N)*t)/((R/EN)-Math.cos((1-N)*t)))))-(E*Math.cos(N*t));
    double y = (R*Math.sin(t))-(Rr*Math.sin(t+Math.atan(Math.sin((1-N)*t)/((R/EN)-Math.cos((1-N)*t)))))-(E*Math.sin(N*t));
    points.add(new Vector3d(x, y));
    }
       
CSG disk = Extrude.points(new Vector3d(0,0, 4.5),points);
disk = disk.difference(new Cylinder(Cr, Cr, 10, 64).toCSG());

// Drive holes
ArrayList<CSG> drivePinHoles = new ArrayList<CSG>();
for (int i = 0; i < Nd; i += 1) {
    drivePinHoles.add(new Cylinder(Dr+E, Dr+E, 10, 64).toCSG()
        .movex(Pd*Math.cos((2*Math.PI*i)/Nd))
        .movey(Pd*Math.sin((2*Math.PI*i)/Nd))
        );
    }


// subtract holes from disk
disk = disk.difference(drivePinHoles);
CSG disk2 = disk

// Move the disk into position
disk = disk.movex(E);
disk = disk.movez(largeBearingH + 4.5);

// disk 2
disk2 = disk2.movex(-E);
disk2 = disk2.movez(largeBearingH + 5 + 4.5);

// ======================================
// Drive shaft
CSG driveShaft = new Cylinder(DCr-E, 31).toCSG();

// Drive Cam
CSG driveCam = new Cylinder(DCr, 5).toCSG();
CSG driveCam2 = driveCam;
driveCam = driveCam.movex(E);
driveCam = driveCam.movez((largeBearingH + 4));

driveCam3 = driveCam2.movex(-E);
driveCam3 = driveCam3.movez((largeBearingH + 5 + 4));

driveShaft = driveShaft.union(driveCam);
driveShaft = driveShaft.union(driveCam3);
driveShaft = driveShaft.difference(new Cylinder(SA, 31).toCSG());

// ======================================
// Define outer ring
CSG outerRing = new Cylinder(Rg, Rg, largeBearingH*2 + 10 + 1, 64).toCSG();
outerRing = outerRing.difference(new Cylinder(R, R, 20, 64).toCSG());

CSG bearingSeat = new Cylinder(LargeBearingR, LargeBearingR, largeBearingH, 64).toCSG();
bearingSeat = bearingSeat.union(new Cylinder(LargeBearingR-2, LargeBearingR-2, largeBearingH+1, 64).toCSG().movez(-0.5));

// Bottom bearing seat
outerRing = outerRing.difference(bearingSeat);
// Top bearing seat
outerRing = outerRing.difference(bearingSeat.movez(largeBearingH + 11));

// move the outer ring into position
outerRing = outerRing.movez(4);

// Pins
ArrayList<CSG> pins = new ArrayList<CSG>();
for (int i = 0; i < N; i += 1) {
    pins.add(new Cylinder(Rr, 10).toCSG()
        .movex(R*Math.cos((2*Math.PI*i)/N))
        .movey(R*Math.sin((2*Math.PI*i)/N))
        .movez(largeBearingH + 4.5)
        );

    outerRing = outerRing.union(new Cylinder(Rr, 10).toCSG()
        .movex(R*Math.cos((2*Math.PI*i)/N))
        .movey(R*Math.sin((2*Math.PI*i)/N))
        .movez(largeBearingH + 4.5)
        );
    
    }

ArrayList<CSG> outerAssembly = [
                                    outerRing,
                                   // pins
                                ];

// ======================================
// Output Side Bearing Support
CSG outputSupport = new Cylinder(largeBearingInnerR, largeBearingInnerR, largeBearingH + 3, 64).toCSG();
outputSupport = outputSupport.union(new Cylinder(largeBearingInnerR+2, largeBearingInnerR+2, 3, 64).toCSG());

// Output support center hole
outputSupport = outputSupport.difference(new Cylinder(Cr, Cr, 4, 64).toCSG());
outputSupport = outputSupport.difference(new Cylinder(Cr-1, Cr-1, (largeBearingH + 4), 64).toCSG());

// move the output support into position
outputSupport = outputSupport.rotx(180);
outputSupport = outputSupport.movez(32);

//======================================
// Drive Side Bearing Support
CSG intputSupport = new Cylinder(largeBearingInnerR, largeBearingInnerR, largeBearingH + 4, 64).toCSG();
intputSupport = intputSupport.union(new Cylinder(Rg, Rg, 3, 64).toCSG());

// add a bearing support flange
CSG flangeBearingSupport = new Cylinder(largeBearingInnerR+2, largeBearingInnerR+2, 4, 64).toCSG();
intputSupport = intputSupport.union(flangeBearingSupport);

// Intput support center hole
intputSupport = intputSupport.difference(new Cylinder(Cr, Cr, 4, 64).toCSG().movez(largeBearingH));
intputSupport = intputSupport.difference(new Cylinder(Cr-1, Cr-1, (largeBearingH + 4), 64).toCSG());


// Flage holes
CSG flangeHole = new Cylinder(3.2/2.0, 3).toCSG();
int flangeHoleCount = 6;
double flangeHoleRadius = (Rg-(largeBearingInnerR+2))/2 + largeBearingInnerR+2;
for (int i = 0; i < flangeHoleCount; i += 1) {
    intputSupport = intputSupport.difference(flangeHole
        .movex(flangeHoleRadius*Math.cos((2*Math.PI*i)/flangeHoleCount))
        .movey(flangeHoleRadius*Math.sin((2*Math.PI*i)/flangeHoleCount))
        );
}

// Drive Pins
ArrayList<CSG> drivePins = new ArrayList<CSG>();
print("Drive Pin Length: " + (10+largeBearingH*2) + "\n");
CSG drivePin = new Cylinder(Dr, 10+largeBearingH*2).toCSG();
CSG drivePinHole = new Cylinder(Dr+.1, 10+largeBearingH*2).toCSG();
for (int i = 0; i < Nd; i += 1) {
    drivePins.add(drivePin
        .movex(Pd*Math.cos((2*Math.PI*i)/Nd))
        .movey(Pd*Math.sin((2*Math.PI*i)/Nd))
        .movez(3)
        );
    
    intputSupport = intputSupport.difference(drivePinHole
        .movex(Pd*Math.cos((2*Math.PI*i)/Nd))
        .movey(Pd*Math.sin((2*Math.PI*i)/Nd))
        .movez(0)
        );

    outputSupport = outputSupport.difference(drivePinHole
        .movex(Pd*Math.cos((2*Math.PI*i)/Nd))
        .movey(Pd*Math.sin((2*Math.PI*i)/Nd))
        .movez(3)
        );

    }

// Subassemblie 
ArrayList<CSG> inputSideAssembly = [
                                    intputSupport,
                                    drivePins
                                ];

disk.setName("disk");
disk.setColor(javafx.scene.paint.Color.CYAN);
disk2.setColor(javafx.scene.paint.Color.RED);

// ======================================
ArrayList<CSG> all = [
                        disk,
                        disk2,
                        driveShaft,
                        inputSideAssembly,
                        outputSupport,
                        outerAssembly
                    ];

// ======================================
// Section View
Boolean enableSectionView = true;
if (enableSectionView) {
    CSG sectionView = new Cube(Rg*2, Rg*2, 100).toCSG().movey(-Rg).movez(10);
    for (int i = 0; i < all.size(); i += 1) {
        // if CSG is a list
        if (all[i] instanceof ArrayList) {
            for (int j = 0; j < all[i].size(); j += 1) {
                if (all[i][j] instanceof ArrayList) {
                    for (int k = 0; k < all[i][j].size(); k += 1) {
                        all[i][j][k] = all[i][j][k].difference(sectionView);
                    }
                } else {
                    all[i][j] = all[i][j].difference(sectionView);
                }
            }
        } else {
            all[i] = all[i].difference(sectionView);
        }
    }
}


return [all];