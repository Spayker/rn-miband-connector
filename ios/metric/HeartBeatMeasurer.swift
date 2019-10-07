import Foundation
import CoreBluetooth

@objc(HeartBeatMeasurer) class HeartBeatMeasurer: NSObject {
  
  private var miBand: CBPeripheral!
  private var hrControlPointCharacteristic: CBCharacteristic?
  private var hrMeasurementCharacteristic: CBCharacteristic?
  private var sensorService: CBService?
  private var sensorCharacteristic: CBCharacteristic?
  
  @objc static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  @objc func startHeartRateCalculation(_ callback: RCTResponseSenderBlock) {
    print("startHeartRateCalculation called")
    callback([NSNull(), -1])
  }
  
  @objc func getHeartRate(_ callback: RCTResponseSenderBlock){
    callback([NSNull(), -1])
  }
  
  func onHeartRateReceived(_ heartRate: Int) {
    print("BPM: \(heartRate)")
  }
  
}





