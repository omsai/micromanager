project(MMDevice)
add_library(MMDevice STATIC 
  ModuleInterface.cpp
  Property.cpp
  DeviceUtils.cpp
  ImgBuffer.cpp
  )

set_target_properties( MMDevice PROPERTIES POSITION_INDEPENDENT_CODE TRUE )