cmake_minimum_required(VERSION 3.27)
project(Chess)

set(CMAKE_CXX_STANDARD 20)

include(FetchContent)
FetchContent_Declare(
        fmt
        GIT_REPOSITORY https://github.com/fmtlib/fmt
        GIT_TAG 10.2.1
)
FetchContent_MakeAvailable(fmt)


add_library(Chess SHARED ChessBoard.cpp)


target_include_directories(Chess PRIVATE "C:/Program Files/Java/jdk-21/include")
target_include_directories(Chess PRIVATE "C:/Program Files/Java/jdk-21/include/win32")

target_link_libraries(Chess fmt)
