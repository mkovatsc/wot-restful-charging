describe('Testing the state machine', function() {

  beforeEach(module('CarUI'));

  it('contains all desired states', inject(function(stateMachine) {
    var states = ["init",
                  "chargeParameterDiscovery",
                  "cableCheck",
                  "preCharge",
                  "powerDelivery",
                  "currentDemand",
                  "weldingDetection",
                  "sessionStop"];
    expect(stateMachine.getStates()).toEqual(states);
  }));

  // TODO further tests

});
