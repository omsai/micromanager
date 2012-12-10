cmake_minimum_required(VERSION 2.6)
project(MMLIBS)

## Options
option(USE_SWIG_BINDINGS "Java and Python bindings" ON)

## Main package
set(Boost_USE_STATIC_LIBS OFF)
set(Boost_USE_MULTITHREADED ON) 
set(Boost_USE_STATIC_RUNTIME OFF)
find_package(Boost REQUIRED COMPONENTS date_time exception system thread)
# Top-level include since Boost is also used by DeviceAdapters
include_directories(${Boost_INCUDE_DIRS})
# Top-level include since MMCore is used by bindings, DeviceAdapters,
# and example DeviceAdapters
include_directories("${PROJECT_SOURCE_DIR}/MMCore")
add_subdirectory(MMCore)
#add_subdirectory(MMDevice)
# TODO Make SimpleAutoFocus DeviceAdapter include
# ${PROJECT_SOURCE_DIR}/Util
#add_subdirectory(DeviceAdapters)

## Bindings
if(USE_SWIG_BINDINGS)
  find_package(SWIG REQUIRED)
#  add_subdirectory(MMCoreJ_wrap)
#  add_subdirectory(MMCorePy_wrap)
endif()

## Documentation
#add_subdirectory(DeviceKit)
#add_subdirectory(Tutorial)
#add_subdirectory(doc)
