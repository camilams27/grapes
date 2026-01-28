/// Modelo do jogador retornado pela API
class Player {
  final String id;
  final String nickname;
  final String email;
  final int level;
  final int xp;

  Player({
    required this.id,
    required this.nickname,
    required this.email,
    required this.level,
    required this.xp,
  });

  /// Factory method para converter JSON em objeto Player
  factory Player.fromJson(Map<String, dynamic> json) {
    return Player(
      id: json['id']?.toString() ?? '',
      nickname: json['nickname'] ?? '',
      email: json['email'] ?? '',
      level: json['level'] ?? 1,
      xp: json['xp'] ?? json['experience'] ?? 0,
    );
  }

  /// Converte Player para JSON (útil para requisições)
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'nickname': nickname,
      'email': email,
      'level': level,
      'xp': xp,
    };
  }

  /// Calcula XP necessário para o próximo nível
  int get xpToNextLevel => (level * 100) - xp;

  /// Porcentagem de progresso para o próximo nível
  double get levelProgress => xp / (level * 100);
}
