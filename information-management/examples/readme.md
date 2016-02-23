## Runnning collector

The collector include 4 artifact:

1. collector-1.0.jar

2. Set of tranformers: TransformAndroidSensor-1.0.jar, TransformOpenIoTSensor-1.0.jar

3. Source configuration: info-source.conf

An example of running is:

```shellscript
java -cp "collector-1.0.jar:TransformAndroidSensor-1.0.jar:TransformOpenIoTSensor-1.0.jar" at.ac.tuwien.dsg.cloud.salsa.collector.Main "$@"
```


## Data examples
 
 1.android.sensor: the list of sensor on Sony Z3 phone. Queried by Android API and export to JSON from android.hardware.sensor model.
 
 2.OpenIoT data: Gather from API at http://130.206.80.47:5371/m2m/v2/services/OpenIoT
 
 - Query the catalog: http://130.206.80.47:5371/m2m/v2/services/OpenIoT/model/
 
 - Query the list of assets: http://130.206.80.47:5371/m2m/v2/services/OpenIoT/assets/
 
 - Query information of particular access: http://130.206.80.47:5371/m2m/v2/services/OpenIoT/assets/[assetsID]
Note: query on 23-02-2016, there are 228 assets on this 
 
