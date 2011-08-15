//
//  MobSquid.h
//  RV
//
//  Created by Radu Spineanu on 6/24/11.
//  Copyright 2011 MobSquid. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>

@class SensorEyes;

@interface MobSquid : NSObject {
    NSString *version;
    
    /* Stores your applicationToken */
    NSString *applicationToken;
    
    /* Stores the active contexts */
    NSMutableDictionary *context;

    /* Used Internally */
    SensorEyes *sensorEyes;
}

@property (nonatomic, retain) NSString *applicationToken;
@property (nonatomic, retain) NSString* version;
@property (nonatomic, retain) NSMutableDictionary *context;
@property (nonatomic, retain) SensorEyes *sensorEyes;

/* Public Methods */
- (void)start:(NSString *)appToken;
- (void)receiveLocation:(CLLocation *)location;
/* End of Public Methods */

/* Private Methods */
- (void)event:(NSString *)name properties:(NSDictionary *)properties;
- (void)debugMessage:(NSString *)message;
- (void)debugSensorHistory;
/* End of Private Methods */

@end
