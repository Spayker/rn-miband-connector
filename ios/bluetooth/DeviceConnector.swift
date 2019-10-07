import Foundation
import CoreBluetooth

@objc(DeviceConnector) class DeviceConnector: NSObject {
  
  var centralManager: CBCentralManager!
  var miBandPeripheral: CBPeripheral!
  
  private var hrControlPointCharacteristic: CBCharacteristic?
  private var alertCharacteristic: CBCharacteristic?
  private var hrMeasurementCharacteristic: CBCharacteristic?
  private var authChar: CBCharacteristic?
  private var authService: CBService?
  private var authDescriptor: CBDescriptor?
  
  @objc static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  @objc func enableBTAndDiscover(_ callback: RCTResponseSenderBlock) {
    print("enableBTAndDiscover called")
    centralManager = CBCentralManager(delegate: self, queue: nil)
    callback([NSNull(), 0])
  }
  
  @objc func getDeviceBondLevel(_ callback: RCTResponseSenderBlock){
    callback([NSNull(), miBandPeripheral.state.rawValue])
  }
  
}

extension DeviceConnector: CBCentralManagerDelegate {
  
  func centralManagerDidUpdateState(_ central: CBCentralManager) {
    switch central.state {
    case .unknown:
      print("central.state is .unknown")
    case .resetting:
      print("central.state is .resetting")
    case .unsupported:
      print("central.state is .unsupported")
    case .unauthorized:
      print("central.state is .unauthorized")
    case .poweredOff:
      print("central.state is .poweredOff")
    case .poweredOn:
      print("central.state is .poweredOn")
      startPeripheralScanning()
    @unknown default:
      print("deault central.state is .unknown")
    }
  }
  
  func startPeripheralScanning() {
    centralManager.scanForPeripherals(withServices: nil)
  }
  
  func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral,
                      advertisementData: [String : Any], rssi RSSI: NSNumber) {
    print(peripheral)
    let deviceName = peripheral.name
    
    if(deviceName != nil && deviceName?.contains("Mi Band") ?? false) {
      miBandPeripheral = peripheral
      miBandPeripheral.delegate = self
      centralManager.stopScan()
      centralManager.connect(miBandPeripheral)
    }
  }
  
  func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
    print("MiBand 3 connected...")
    miBandPeripheral.discoverServices(nil)
  }
}

extension DeviceConnector: CBPeripheralDelegate {
  
  func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
    guard let services = peripheral.services else { return }
    
    for service in services {
      if service.uuid.uuidString.contains("FEE1"){
        authService = service
      }
      peripheral.discoverCharacteristics(nil, for: service)
    }
  }
  
  func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {

    service.characteristics?.forEach { characteristic in
      guard let miCharacteristicID = MiCharacteristicID(rawValue: characteristic.uuid.uuidString) else { return }
      
      switch miCharacteristicID {
        case .authCharacteristic:
          if characteristic.properties.contains(.read) {
            peripheral.readValue(for: characteristic)
          }
          if characteristic.properties.contains(.notify) {
            peripheral.setNotifyValue(true, for: characteristic)
          }
          authChar = characteristic
        default: break
      }
    }
  }
  
  func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
    print("didUpdateValueFor char: \(characteristic.uuid.uuidString)")
    
    let data = characteristic.value
    let dataString = String(data: data!, encoding: String.Encoding.utf8)
    print("Data: \(String(describing: dataString))")
  }
  
  func peripheral(_ peripheral: CBPeripheral, didUpdateNotificationStateFor characteristic: CBCharacteristic, error: Error?) {
    print("didUpdateNotificationStateFor char: \(characteristic.uuid.uuidString) \(characteristic.isNotifying)")
    
    guard let authCharac = authChar else {
      print("authChar: Invalid setup!")
      return
    }
    
    let authKey: [UInt8] = [0x01, 0x00, 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x59, 0x37]
    let data2 = NSData(bytes: authKey, length: authKey.count)
    peripheral.writeValue(data2 as Data, for: authCharac, type: .withResponse)
    print("didUpdateNotificationStateFor written char: \(characteristic.uuid.uuidString)")
  }
  
  func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
    print("didWriteValueFor char: \(characteristic.uuid.uuidString)")
    
    guard let data = characteristic.value else {
      print("data: nil!")
      return
    }
    let dataString = String(data: data, encoding: String.Encoding.utf8)
    print("didWriteValueFor char data: \(String(describing: dataString))")
  }
  
}

enum MiCharacteristicID: String {
  case dateTime = "2A2B"
  case alert = "2A06"
  case authCharacteristic = "00000009-0000-3512-2118-0009AF100700"
}
