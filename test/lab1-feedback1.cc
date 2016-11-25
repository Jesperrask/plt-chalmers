int main(){
    // should not fail
    **x;
    // should not fail
    !!x;
    //should fail!
    x()();
}
