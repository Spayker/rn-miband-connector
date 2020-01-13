#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(DeviceConnector, NSObject)

  RCT_EXTERN_METHOD(enableBTAndDiscover: (RCTResponseSenderBlock)callback)
  RCT_EXTERN_METHOD(getDeviceBondLevel: (RCTResponseSenderBlock)callback)

@end