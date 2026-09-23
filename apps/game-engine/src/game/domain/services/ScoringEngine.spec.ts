import { ScoringEngine } from './ScoringEngine';

describe('ScoringEngine', () => {
  it('should return 0 points when the answer is incorrect', () => {
    const points = ScoringEngine.calculateSpeedPoints(false, 10000, 2000, 1000);
    expect(points).toBe(0);
  });

  it('should return maximum points when answered instantly (timeTaken = 0)', () => {
    const points = ScoringEngine.calculateSpeedPoints(true, 10000, 0, 1000);
    expect(points).toBe(1000);
  });

  it('should return 50% base points when answered at the last millisecond', () => {
    const points = ScoringEngine.calculateSpeedPoints(true, 10000, 10000, 1000);
    expect(points).toBe(500);
  });

  it('should calculate proportional speed bonus', () => {
    // 50% time elapsed -> half of speed bonus (250) + base (500) = 750
    const points = ScoringEngine.calculateSpeedPoints(true, 10000, 5000, 1000);
    expect(points).toBe(750);
  });
});
