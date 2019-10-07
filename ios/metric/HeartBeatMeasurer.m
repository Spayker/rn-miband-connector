//
//  HeartBeatMeasurer.m
//  sbp
//
//  Created by Alex on 01/09/2019.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"

@interface RCT_EXTERN_MODULE(HeartBeatMeasurer, NSObject)

RCT_EXTERN_METHOD(startHeartRateCalculation: (RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(getHeartRate: (RCTResponseSenderBlock)callback)

@end
