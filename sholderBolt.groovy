
// Sholder Bolt Class
class SholderBolt {
    double threadDiameter  =  3.5052;  // 0.138in = 6-32
    double threadLength    =  4.7752;  // 0.188in 
    double sholderDiameter =  3.9624;  // 0.156in 
    double sholderLength   = 25.4000;  // 1.000in 
    double headDiameter    =  7.1374;  // 0.281in
    double headLength      =  3.1750;  // 0.125in = 1/8in
    
    // Tolernace
    double sholderDiameterTol = -0.0254; // +0in    -0.001in
    double sholderLengthTol   =  0.0508; // 0.002in -0in

    // Constructor
    SholderBolt() {
        println "Sholder Bolt Object Created"
    }

    def getBoltNom() {
        CSG BoltHead    = new Cylinder(headDiameter/2,    headLength).toCSG()
        CSG BoltSholder = new Cylinder(sholderDiameter/2, sholderLength).toCSG()
        CSG BoltThread  = new Cylinder(threadDiameter/2,  threadLength).toCSG()
        CSG Bolt = BoltHead
                    .union(BoltSholder.movez(headLength))
                    .union(BoltThread.movez(headLength + sholderLength));
        return Bolt;
    }
}