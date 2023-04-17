@objc(Kiosk)
class Kiosk: NSObject {
  @objc(init:)
  func `init`(options: NSDictionary) -> Void {
    
  }
  
  @objc(enable:withRejecter:)
  func enable(resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(false)
  }
    
  @objc(disable:withRejecter:)
  func disable(resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(false)
  }
}
