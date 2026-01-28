import 'package:dio/dio.dart';
import 'auth_interceptor.dart';

/// Cliente HTTP Dio configurado para o Grapes API
/// Usa singleton pattern para reutilização eficiente
class DioClient {
  static DioClient? _instance;
  late final Dio dio;

  // URL do backend - 10.0.2.2 é o localhost do emulador Android
  static const String baseUrl = 'http://10.0.2.2:8080';

  DioClient._internal() {
    dio = Dio(
      BaseOptions(
        baseUrl: baseUrl,
        connectTimeout: const Duration(seconds: 10),
        receiveTimeout: const Duration(seconds: 10),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ),
    );

    // Adiciona interceptor de autenticação
    dio.interceptors.add(AuthInterceptor());

    // Interceptor de log para debug (pode remover em produção)
    dio.interceptors.add(
      LogInterceptor(requestBody: true, responseBody: true, error: true),
    );
  }

  /// Retorna instância singleton do DioClient
  static DioClient get instance {
    _instance ??= DioClient._internal();
    return _instance!;
  }

  /// Acesso direto ao Dio configurado
  static Dio get http => instance.dio;

  /// Limpa a instância (útil para logout)
  static void reset() {
    _instance = null;
  }
}
