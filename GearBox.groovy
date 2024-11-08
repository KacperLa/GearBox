

ArrayList<CSG> makeGearBox (
    double outerRadius, // Radius of Gear box
    double ringGearPitchRadius, // pitch radius of fixed ring pins
    double eccentricityOffset, // eccentricity offset   
    int numberOfRingPins, // number of ring pins
    double ringPinRadius, // radius of ring pins    
    double drivePinRadius, // drive pin radius
    int numberOfDrivePins, // number of drive pins
    double drivePinPitchRadius // pitch radius of drive pins
    )
{
    // Basic Paramaters
    // Cycloid gear disk
    double R  = ringGearPitchRadius;  // pitch radius of fixed ring pins
    double E  = eccentricityOffset;   // eccentricity offset
    double Rr = ringPinRadius;        // radius of ring pins
    double N  = numberOfRingPins;     // number of ring pins

    double Dr = drivePinRadius;    // drive pin radius
    double Nd = numberOfDrivePins;    // number of drive pins

    double flangeH = 3;

    // Large bearing definition
    double LargeBearingR = 58/2.0;      // Large bearing radius
    double largeBearingH = 7;           // Large bearing height
    double largeBearingInnerR = 45/2.0; // Large bearing inner radius
    
    CSG largeBearing = new Cylinder(LargeBearingR, LargeBearingR, largeBearingH, 64).toCSG();
    largeBearing = largeBearing.union(new Cylinder(LargeBearingR-2, LargeBearingR-2, largeBearingH+2, 64).toCSG().movez(-1));
    
    // XLarge bearing definition
    double mediumBearingOuterR = 27/2.0;      // Large bearing radius
    double mediumBearingH = 4;           // Large bearing height
    double mediumBearingInnerR = 20/2.0; // Large bearing inner radius

    CSG mediumBearing = new Cylinder(mediumBearingOuterR, mediumBearingOuterR, mediumBearingH, 64).toCSG();
    mediumBearing = mediumBearing.union(new Cylinder(mediumBearingOuterR-1.5, mediumBearingOuterR-1.5, mediumBearingH+2, 64).toCSG().movez(-1));

    // Small bearing definition
    double SmallBearingR = 23.0/2.0;      // Small bearing radius
    double smallBearingH = 4;           // Small bearing height
    double smallBearingInnerR = 17.0/2.0; // Small bearing inner radius

    CSG smallBearing = new Cylinder(SmallBearingR, SmallBearingR, smallBearingH, 64).toCSG();
    smallBearing = smallBearing.union(new Cylinder(SmallBearingR-1, SmallBearingR-1, smallBearingH+2, 64).toCSG().movez(-1));

    // Dead Shaft definition
    double deadShaftOuterR = 12.0/2.0;  // Dead Shaft outer radius
    double deadShaftInnerR = 10.0/2.0;  // Dead Shaft innter radius
   
    CSG deadShaft = new Cylinder(deadShaftOuterR, deadShaftOuterR, 52, 64).toCSG();
    deadShaft = deadShaft.difference(new Cylinder(deadShaftInnerR, deadShaftInnerR, 52, 64).toCSG());
    deadShaft.setName("deadShaft");
    deadShaft.setColor(javafx.scene.paint.Color.GRAY);

    double inputShaftInnerR = deadShaftOuterR+0.75; // Drive Shaft apatrum radius
    double inputShaftOuterR = smallBearingInnerR-eccentricityOffset; // Drive Shaft outer radius

    double EN = E/N;

    // Calculate the total height of the gear box
    double gearBoxHeight = (largeBearingH  * 2.0 ) + 1.5 + ((smallBearingH + 1) * 2.0) + 0.5;                      // Space between cycloid disks

    // Ring Gear
    CSG ringGear = new Cylinder(outerRadius , outerRadius, gearBoxHeight, 64).toCSG();
    ringGear = ringGear.difference(new Cylinder(ringGearPitchRadius, ringGearPitchRadius, gearBoxHeight, 64).toCSG());  
    // Bottom bearing seat
    ringGear = ringGear.difference(largeBearing);
    // Top bearing seat
    ringGear = ringGear.difference(largeBearing.movez(gearBoxHeight - largeBearingH));

    ringGear = ringGear.movez(flangeH + 1);

    //outerRing = outerRing.difference(new Cylinder(R, R, 20, 64).toCSG());

    // Pins
    ArrayList<CSG> outputPins = new ArrayList<CSG>();
    for (int i = 0; i < N; i += 1) {
        outputPins.add(new Cylinder(Rr, 10).toCSG()
            .movex(R*Math.cos((2*Math.PI*i)/N))
            .movey(R*Math.sin((2*Math.PI*i)/N))
            .movez(largeBearingH + 4.5)
            );

        ringGear = ringGear.union(new Cylinder(Rr, 10).toCSG()
            .movex(R*Math.cos((2*Math.PI*i)/N))
            .movey(R*Math.sin((2*Math.PI*i)/N))
            .movez(largeBearingH + 4.5)
            );
        }

    //outputPins.setName("outputPins");
    ringGear.setName("ringGear");
    //outputPins.setColor(javafx.scene.paint.Color.GREEN);
    ringGear.setColor(javafx.scene.paint.Color.PINK);

    // Disk
    // 100 points on the cycloid
    ArrayList<Vector3d> points = []
    int segments = 150;

    for (int i = 0; i < segments; i += 1) {
        double t = (i * ((2*Math.PI)/segments));
        double x = (R*Math.cos(t))-(Rr*Math.cos(t+Math.atan(Math.sin((1-N)*t)/((R/EN)-Math.cos((1-N)*t)))))-(E*Math.cos(N*t));
        double y = (R*Math.sin(t))-(Rr*Math.sin(t+Math.atan(Math.sin((1-N)*t)/((R/EN)-Math.cos((1-N)*t)))))-(E*Math.sin(N*t));
        points.add(new Vector3d(x, y));
        }
       
    double diskHeight = smallBearingH + 1;
    CSG disk = Extrude.points(new Vector3d(0,0, diskHeight),points);
    disk = disk.difference(mediumBearing);

    // Drive holes
    ArrayList<CSG> drivePinHoles = new ArrayList<CSG>();
    double drivePinHoleRadius = drivePinRadius + eccentricityOffset;
    CSG drivePinHole = new Cylinder(drivePinHoleRadius, drivePinHoleRadius, diskHeight, 64).toCSG();
    for (int i = 0; i < numberOfDrivePins; i += 1) {
        disk = disk.difference(drivePinHole
            .movex(drivePinPitchRadius*Math.cos((2*Math.PI*i)/Nd))
            .movey(drivePinPitchRadius*Math.sin((2*Math.PI*i)/Nd))
            );
        }

    CSG disk2 = disk

    // Move the disk into position
    disk = disk.movex(eccentricityOffset);
    disk = disk.movez(largeBearingH + 4.5);

    // disk 2
    disk2 = disk2.rotz(180);
    disk2 = disk2.movex(-eccentricityOffset);
    disk2 = disk2.movez(largeBearingH + 5 + 4.5);

    disk.setName("disk");
    disk2.setName("disk2");
    disk.setColor(javafx.scene.paint.Color.CYAN);
    disk2.setColor(javafx.scene.paint.Color.RED);

    // Output side support
    //=====================================
    CSG outputSupport = new Cylinder(largeBearingInnerR, largeBearingInnerR, largeBearingH + flangeH, 64).toCSG();
    outputSupport = outputSupport.union(new Cylinder(largeBearingInnerR+2, largeBearingInnerR+2, 3, 64).toCSG());

    // Output support center hole
    outputSupport = outputSupport.difference(smallBearing.movez(largeBearingH-1));
    outputSupport = outputSupport.difference(new Cylinder(SmallBearingR-1, SmallBearingR-1, 1, 64).toCSG().movez(5));
    outputSupport = outputSupport.difference(new Cylinder(deadShaftOuterR, largeBearingH).toCSG());

    // move the output support into position
    outputSupport = outputSupport.rotx(180);
    outputSupport = outputSupport.movez(32);

    // Motor mount
    CSG motorMount = new Cylinder(outerRadius, outerRadius, 20, 64).toCSG();
    motorMount = motorMount.difference(new Cylinder(outerRadius-6, outerRadius-6, 18, 64).toCSG().movez(2));
    motorMount = motorMount.union(new Cylinder(14/2, 14/2, 14, 64).toCSG());  
    motorMount = motorMount.union(new Cylinder(16/2, 16/2, 5, 64).toCSG());  
    // Key
    motorMount = motorMount.union(new Cube(2, 2, 15).toCSG().movez(15/2).movex(14/2));

    motorMount = motorMount.difference(new Cylinder(deadShaftOuterR, deadShaftOuterR, 18, 64).toCSG());
    // Cable hole
    motorMount = motorMount.difference(new Cylinder(10/2, 10/2, 20, 64).toCSG().movex(15));

    // cut everyting but center pillar
    // motorMount = motorMount.intersect(new Cylinder(10, 10, 30, 64).toCSG());

    // Input side support
    //=====================================
    CSG intputSupport = new Cylinder(outerRadius, outerRadius, flangeH, 64).toCSG();
    intputSupport = intputSupport.union(new Cylinder(largeBearingInnerR, largeBearingInnerR, largeBearingH + 1 + flangeH, 64).toCSG());
    intputSupport = intputSupport.union(new Cylinder(largeBearingInnerR+2, largeBearingInnerR+2, 4, 64).toCSG());

    // center hole
    // intputSupport = intputSupport.difference(smallBearing.movez(largeBearingH));
    intputSupport = intputSupport.difference(new Cylinder(13, 13, (largeBearingH + flangeH +1), 64).toCSG());

    // Flage holes
    CSG flangeHole = new Cylinder(3.2/2.0, 50).toCSG();
    CSG flangeHoleTap = new Cylinder(2.8/2.0, 50).toCSG();
    int flangeHoleCount = 6;
    double flangeHoleRadius = outerRadius - 3;
    for (int i = 0; i < flangeHoleCount; i += 1) {
        intputSupport = intputSupport.difference(flangeHole
            .movex(flangeHoleRadius*Math.cos((2*Math.PI*i)/flangeHoleCount))
            .movey(flangeHoleRadius*Math.sin((2*Math.PI*i)/flangeHoleCount))
            );
        
        motorMount = motorMount.difference(flangeHoleTap
            .movex(flangeHoleRadius*Math.cos((2*Math.PI*i)/flangeHoleCount))
            .movey(flangeHoleRadius*Math.sin((2*Math.PI*i)/flangeHoleCount))
            );
    }

    // Drive Pins
    double statorPinLength = 30;
    ArrayList<CSG> drivePins = new ArrayList<CSG>();
    CSG drivePin = new Cylinder(drivePinHoleRadius, statorPinLength).toCSG();
    CSG drivePinHolePressFit = new Cylinder(drivePinRadius+0.1, statorPinLength).toCSG();
    for (int i = 0; i < numberOfDrivePins; i += 1) {
        drivePins.add(drivePin
            .movex(drivePinPitchRadius*Math.cos((2*Math.PI*i)/Nd))
            .movey(drivePinPitchRadius*Math.sin((2*Math.PI*i)/Nd))
            );
        
        intputSupport = intputSupport.difference(drivePinHolePressFit
            .movex(drivePinPitchRadius*Math.cos((2*Math.PI*i)/Nd))
            .movey(drivePinPitchRadius*Math.sin((2*Math.PI*i)/Nd))
            );

        outputSupport = outputSupport.difference(drivePinHolePressFit
            .movex(drivePinPitchRadius*Math.cos((2*Math.PI*i)/Nd))
            .movey(drivePinPitchRadius*Math.sin((2*Math.PI*i)/Nd))
            .movez(3)
            );
        }



    // Drive shaft
    // ======================================
    CSG driveShaft = new Cylinder(smallBearingInnerR, smallBearingInnerR, 23, 64).toCSG();

    // Drive Cam
    CSG driveCam = new Cylinder(mediumBearingInnerR, mediumBearingInnerR, 5, 64).toCSG();
    CSG driveCam2 = driveCam;

    driveCam = driveCam.movex(eccentricityOffset);
    driveCam = driveCam.movez((largeBearingH + 1.5));

    driveCam2 = driveCam2.movex(-E);
    driveCam2 = driveCam2.movez((largeBearingH + 1.5 + 5));

    driveShaft = driveShaft.union(driveCam);
    driveShaft = driveShaft.union(driveCam2);
    driveShaft = driveShaft.union(new Cylinder(smallBearingInnerR+.6, smallBearingInnerR+.6, 5.5, 64).toCSG().movez((14)));


    driveShaft = driveShaft.difference(new Cylinder(inputShaftInnerR, inputShaftInnerR, 36, 64).toCSG());
    driveShaft.setColor(javafx.scene.paint.Color.YELLOW);

    // Motor Bell
    // ======================================
    double motorBellR = 50/2.0;
    double motorBellH = 8;
    double xsmallBearingR = 18/2.0;
    double xsmallBearingH = 4;

    CSG motorBell = new Cylinder(motorBellR+1, motorBellR+1, motorBellH+2, 64).toCSG();
    motorBell = motorBell.union(new Cylinder(xsmallBearingR+3, motorBellH+17).toCSG());

    motorBell = motorBell.difference(new Cylinder(motorBellR, motorBellR, motorBellH, 64).toCSG());
    motorBell = motorBell.difference(new Cylinder(smallBearingInnerR, motorBellH+17, 64).toCSG().movez(motorBellH+xsmallBearingH+5));
    motorBell = motorBell.difference(new Cylinder(inputShaftInnerR, motorBellH+17, 64).toCSG());

    motorBell = motorBell.difference(new Cylinder(xsmallBearingR, xsmallBearingH, 64).toCSG().movez(motorBellH));

    motorBell = motorBell.movez(-(motorBellH+6));

   
    return [
            disk,
            disk2,
            //outputPins, 
            ringGear,
            intputSupport,
            outputSupport,
            deadShaft.movez(-20),
            driveShaft.movez(3),
            motorBell,
            motorMount.movez(-20)
            ];
}

// LengthParameter deadShaftRadius = new LengthParameter("dead shaft radius", 0, [])


ArrayList<CSG> gearBox;
gearBox = makeGearBox( outerRadius         = 32.5,
                       ringGearPitchRadius = 28,
                       eccentricityOffset  = 0.75, 
                       numberOfRingPins = 10, // 9:1
                       ringPinRadius = 2.5,
                       drivePinRadius = 2.5,
                       numberOfDrivePins = 6,
                       drivePinPitchRadius = 19.0
                    );

// ======================================
// Section View
Boolean enableSectionView = true;
if (enableSectionView) {
    CSG sectionView = new Cube(100, 100, 100).toCSG().movey(-50).movez(10);
    for (int i = 0; i < gearBox.size(); i += 1) {
        // if CSG is a list
        if (gearBox[i] instanceof ArrayList) {
            for (int j = 0; j < gearBox[i].size(); j += 1) {
                if (gearBox[i][j] instanceof ArrayList) {
                    for (int k = 0; k < gearBox[i][j].size(); k += 1) {
                        javafx.scene.paint.Color color = gearBox[i][j][k].getColor();
                        gearBox[i][j][k] = gearBox[i][j][k].difference(sectionView);
                        gearBox[i][j][k].setColor(color);
                    }
                } else {
                    javafx.scene.paint.Color color = gearBox[i][j].getColor();
                    gearBox[i][j] = gearBox[i][j].difference(sectionView);
                    gearBox[i][j].setColor(color);
                }
            }
        } else {
            javafx.scene.paint.Color color = gearBox[i].getColor();
            gearBox[i] = gearBox[i].difference(sectionView);
            gearBox[i].setColor(color);
        }
    }
}

return gearBox;