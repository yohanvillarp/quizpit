export class AvatarModel {
  constructor(
    public readonly id: string,
    public readonly name: string,
    public readonly phrase: string,
    public readonly powerName: string,
    public readonly powerDescription: string,
    public readonly effectType: string,
    public readonly powerType: string,
    public readonly isMythic: boolean,
    public readonly isUnlocked: boolean,
  ) {}
}
